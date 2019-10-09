package it.unibo.acdingnet.protelis.node.actor

import akka.actor.AbstractActorWithTimers
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.SensorType
import it.unibo.acdingnet.protelis.node.DestinationNode
import it.unibo.acdingnet.protelis.node.GenericNode
import it.unibo.acdingnet.protelis.node.SensorNode
import it.unibo.acdingnet.protelis.node.UserNode
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ProtelisProgram
import java.io.InputStreamReader
import java.time.Duration


class TimerTick

class ActorNode(val genericNode: GenericNode): AbstractActorWithTimers() {

    override fun preStart() {
        super.preStart()
        timers.startPeriodicTimer(genericNode.deviceUID, TimerTick(), Duration.ofMillis(genericNode.sleepTime))
    }

    override fun createReceive(): Receive =
        ReceiveBuilder.create()
            .match(TimerTick::class.java) { genericNode.runVM() }
            .build()
}

object ActorNodeFactory {

    const val actorSystemName = "protelisNodes"
    private const val configPath = "/configuration/actor/LocalActor.conf"
    private val localConf: Config by lazy { ConfigFactory.parseReader(
        InputStreamReader(
            ActorNodeFactory::class.java.getResourceAsStream(configPath)))
    }
    private val actorSystem by lazy { ActorSystem.create(
        actorSystemName,
        localConf
    ) }

    fun createDestinationNode(protelisProgram: ProtelisProgram, sleepTime: Long, mqttAddress: String, applicationUID: String,
                              destinationUID: StringUID, position: LatLongPosition) =
        createActorWithNode(
            DestinationNode(
                protelisProgram,
                sleepTime,
                destinationUID,
                applicationUID,
                mqttAddress,
                position
            )
        )

    fun createSensorNode(protelisProgram: ProtelisProgram, sleepTime: Long, mqttAddress: String, applicationUID: String,
                         sensorDeviceUID: StringUID, position: LatLongPosition, sensorTypes: List<SensorType>) =
        createActorWithNode(
            SensorNode(
                protelisProgram,
                sleepTime,
                sensorDeviceUID,
                applicationUID,
                mqttAddress,
                position,
                sensorTypes
            )
        )

    fun createUserNode(protelisProgram: ProtelisProgram, sleepTime: Long, mqttAddress: String, applicationUID: String,
                       userUID: StringUID, position: LatLongPosition, sensorTypes: List<SensorType> = emptyList()) =
        createActorWithNode(
            UserNode(
                protelisProgram,
                sleepTime,
                userUID,
                applicationUID,
                mqttAddress,
                position,
                sensorTypes
            )
        )


    fun createActorWithNode(genericNode: GenericNode) = actorSystem.actorOf(Props.create(ActorNode::class.java) { ActorNode(genericNode) })
}