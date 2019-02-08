package world.gregs.hestia.core.network.codec.inbound.packet

import org.slf4j.Logger
import world.gregs.hestia.core.network.Session
import world.gregs.hestia.core.network.packets.Packet

/**
 * PacketProcessor
 * Processes a single packet finding the handler and resetting the sessions ping timeout
 */
interface PacketProcessor<T> {
    val logger: Logger

    fun process(session: Session, packet: Packet) {
        val handler = getHandler(packet.opcode)

        if (handler == null) {
            if (packet.opcode != 16) {//Stop spam for ping packet
                logger.warn("Unhandled packet: ${packet.opcode} ${packet.readableBytes()}")
            }
            return
        }

        //Reset ping
        session.ping = System.currentTimeMillis()

        //Process the packet
        process(session, handler, packet)
    }

    fun getHandler(opcode: Int): T?

    fun process(session: Session, handler: T?, packet: Packet) {
        if (handler == null) {
            logger.warn("Unhandled packet processing: ${packet.opcode}")
            return
        }
    }
}