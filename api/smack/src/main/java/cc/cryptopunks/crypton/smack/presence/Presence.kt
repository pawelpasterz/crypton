package cc.cryptopunks.crypton.smack.presence

import cc.cryptopunks.crypton.entity.Presence
import cc.cryptopunks.crypton.smack.SmackPresence
import org.jivesoftware.smack.XMPPConnection

class SendPresence(
    connection: XMPPConnection
) : Presence.Api.Send, (Presence) -> Unit by { presence ->
    connection.sendStanza(
        SmackPresence(
            org.jivesoftware.smack.packet.Presence.Type.fromString(
                presence.status.name.toLowerCase()
            )
        )
    )
}