package cc.cryptopunks.crypton.smack.util

import cc.cryptopunks.crypton.entity.Address
import cc.cryptopunks.crypton.entity.CryptonMessage
import cc.cryptopunks.crypton.entity.Presence
import cc.cryptopunks.crypton.entity.Resource
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.forward.packet.Forwarded
import org.jivesoftware.smackx.sid.element.StanzaIdElement
import org.jxmpp.jid.impl.JidCreate

internal fun Forwarded.toCryptonMessage() =
    (forwardedStanza as Message).toCryptonMessage(delayInformation.stamp.time)

internal fun Message.toCryptonMessage(
    timestamp: Long = System.currentTimeMillis()
) = CryptonMessage(
    id = extensions.filterIsInstance<StanzaIdElement>().firstOrNull()?.id ?: stanzaId,
    text = body ?: "",
    from = from.resourceId(),
    to = to.resourceId(),
    timestamp = timestamp,
    stanzaId = stanzaId ?: ""
)


internal fun String.resourceId() = JidCreate.from(toString()).resourceId()

internal fun String.remoteId() = JidCreate.from(toString()).remoteId()

internal fun SmackJid.resourceId() = Resource(
    address = remoteId(),
    resource = resourceOrNull?.toString() ?: ""
)

internal fun SmackJid.remoteId() = Address(
    local = localpartOrNull?.toString() ?: "",
    domain = domain.toString()
)

internal fun SmackPresence.presence() = Presence(
    status = Presence.Status.values()[type.ordinal]
)

internal fun Address.bareJid() = JidCreate.bareFrom(this)