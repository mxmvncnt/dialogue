package io.github.zohrevand.dialogue.core.xmpp

import android.util.Log
import io.github.zohrevand.core.model.data.Account
import io.github.zohrevand.core.model.data.AccountStatus.Online
import io.github.zohrevand.dialogue.core.data.repository.AccountsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import javax.inject.Inject

private const val TAG = "XmppManagerImpl"

class XmppManagerImpl @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : XmppManager {

    private var xmppConnection: XMPPTCPConnection? = null

    private var account: Account? = null

    private val _isAuthenticatedState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isAuthenticatedState: StateFlow<Boolean> = _isAuthenticatedState.asStateFlow()

    private val connectionListener = SimpleConnectionListener()

    override fun getConnection(): XMPPTCPConnection =
        xmppConnection ?: throw NoSuchElementException("Connection is not established.")

    override suspend fun login(account: Account) {
        this.account = account
        xmppConnection = account.login(
            configurationBuilder = ::getConfiguration,
            connectionBuilder = ::XMPPTCPConnection,
            reconnectionManager = ::configureReconnectionManager,
            connectionListener = ::addConnectionListener
        )
    }

    override suspend fun register(account: Account) {
        TODO("Not yet implemented")
    }

    private suspend fun Account.login(
        configurationBuilder: (Account) -> XMPPTCPConnectionConfiguration,
        connectionBuilder: (XMPPTCPConnectionConfiguration) -> XMPPTCPConnection,
        reconnectionManager: (XMPPTCPConnection) -> Unit,
        connectionListener: (XMPPTCPConnection) -> Unit,
    ): XMPPTCPConnection {
        val configuration = configurationBuilder(this)
        val connection = connectionBuilder(configuration)

        reconnectionManager(connection)
        connectionListener(connection)

        return connection.connectAndLogin()
    }

    private fun getConfiguration(account: Account): XMPPTCPConnectionConfiguration =
        XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(account.username, account.password)
            .setXmppDomain(account.domain)
            .build()

    // TODO: this warning is fixed as of IntelliJ 2022.1
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun XMPPTCPConnection.connectAndLogin(): XMPPTCPConnection =
        withContext(ioDispatcher) {
            connect()
            login()

            account?.let { accountsRepository.updateAccount(it.copy(status = Online)) }
            _isAuthenticatedState.update { isAuthenticated }

            Log.d(TAG, "isConnected: $isConnected")
            Log.d(TAG, "isAuthenticated: $isAuthenticated")

            this@connectAndLogin
        }

    private fun configureReconnectionManager(connection: XMPPTCPConnection) {
        ReconnectionManager.getInstanceFor(connection)
            .enableAutomaticReconnection()
    }

    private fun addConnectionListener(connection: XMPPTCPConnection) {
        connection.addConnectionListener(connectionListener)
    }

    override fun onCleared() {
        xmppConnection?.removeConnectionListener(connectionListener)
    }
}
