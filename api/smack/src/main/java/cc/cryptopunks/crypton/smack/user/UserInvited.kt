package cc.cryptopunks.crypton.smack.user

import cc.cryptopunks.crypton.api.ApiScope
import cc.cryptopunks.crypton.entity.User
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jxmpp.jid.impl.JidCreate
import javax.inject.Inject

@ApiScope
class UserInvited @Inject constructor(
    connection: XMPPTCPConnection
) : User.Api.Invited, (User) -> Unit by { user ->
    connection.sendStanza(
        Presence(
            JidCreate.from(user.resourceId),
            Presence.Type.subscribed
        )
    )
}