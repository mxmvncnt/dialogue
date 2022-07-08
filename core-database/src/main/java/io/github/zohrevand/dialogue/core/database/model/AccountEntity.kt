package io.github.zohrevand.dialogue.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.zohrevand.core.model.data.Account
import io.github.zohrevand.core.model.data.AccountStatus

const val NOT_INSERTED_ENTITY_ID = 0L

@Entity(
    tableName = "accounts"
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val username: String,
    val domain: String,
    val password: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    val resource: String,
    val status: AccountStatus
)

fun AccountEntity.asExternalModel() = Account(
    id = id,
    username = username,
    domain = domain,
    password = password,
    displayName = displayName,
    resource = resource,
    status = status
)