package io.github.zohrevand.dialogue.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DialoguePreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {

    fun getConnectionStatus(): Flow<ConnectionStatus> = userPreferences.data
        .map {
            ConnectionStatus(
                availability = it.connectionAvailability,
                authorized = it.connectionAuthorized
            )
        }

    /**
     * Update the [ConnectionStatus].
     */
    suspend fun updateConnectionStatus(connectionStatus: ConnectionStatus) {
        try {
            userPreferences.updateData { currentPreferences ->
                currentPreferences.copy {
                    connectionAvailability = connectionStatus.availability
                    connectionAuthorized = connectionStatus.authorized
                }
            }
        } catch (ioException: IOException) {
            Log.e("DialoguePreferences", "Failed to update user preferences", ioException)
        }
    }

    fun getAccount(): Flow<PreferencesAccount> = userPreferences.data
        .map {
            PreferencesAccount(
                jid = it.accountJid,
                localPart = it.accountLocalPart,
                domainPart = it.accountDomainPart,
                password = it.accountPassword,
                status = it.accountStatus
            )
        }

    /**
     * Update the [PreferencesAccount].
     */
    suspend fun updateAccount(account: PreferencesAccount) {
        try {
            userPreferences.updateData { currentPreferences ->
                currentPreferences.copy {
                    accountJid = account.jid
                    accountLocalPart = account.localPart
                    accountDomainPart = account.domainPart
                    accountPassword = account.password
                    accountStatus = account.status
                }
            }
        } catch (ioException: IOException) {
            Log.e("DialoguePreferences", "Failed to update user preferences", ioException)
        }
    }
}
