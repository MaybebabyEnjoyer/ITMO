package airline

import airline.api.*
import airline.servies.EmailService
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExampleTest {
    @Test
    fun testSimple() {
        val config = AirlineConfig(
            audioAlertsInterval = 1.seconds,
            displayUpdateInterval = 10.milliseconds,
            ticketSaleEndTime = 1.hours - 1.minutes,
        )
        val airlineApplication = AirlineApplication(config, emailService)
        val plane1 = Plane("A312", setOf("1A", "1B", "2A", "2B"))
        val flightId = "111"
        val flightTime = Clock.System.now() + 1.hours

        testAndCancel {
            launch { airlineApplication.run() }
            sleep()

            val booking = airlineApplication.bookingService
            val management = airlineApplication.managementService
            val display = airlineApplication.airportInformationDisplay(this)

            management.scheduleFlight(flightId, flightTime, plane1)
            sleep(100.milliseconds)

            Assertions.assertEquals(1, display.value.departing.size)
            Assertions.assertEquals(flightId, display.value.departing[0].flightId)

            Assertions.assertEquals(1, booking.flightSchedule.size)
            Assertions.assertEquals(flightId, booking.flightSchedule[0].flightId)

            sleep()

            booking.buyTicket(flightId, flightTime, "1A", "1", "Konstantin Bats", "kbats@itmo.ru")
            sleep(100.milliseconds)

            // Here and below you can replace `successfully bought` to any other text
            checkEmails("kbats@itmo.ru", flightId, "1A", "successfully bought")
            Assertions.assertEquals(booking.freeSeats(flightId, flightTime), setOf("1B", "2A", "2B"))

            Assertions.assertEquals(1, display.value.departing.size)
            management.setCheckInNumber(flightId, flightTime, "checkin1")
            sleep()

            checkEmails("kbats@itmo.ru", flightId, "checkin1")

            Assertions.assertEquals(1, display.value.departing.size)
            Assertions.assertEquals("111", display.value.departing[0].flightId)
            Assertions.assertEquals("checkin1", display.value.departing[0].checkInNumber)

            management.delayFlight(flightId, flightTime, flightTime + 1.hours)
            sleep()

            checkEmails("kbats@itmo.ru", flightId, "delayed")

            assertContains(
                display,
                FlightShortInfo(
                    flightId,
                    flightTime,
                    checkInNumber = "checkin1",
                    actualDepartureTime = flightTime + 1.hours,
                ),
                checkIn = true,
                actualTime = true,
            )
        }
    }

    @Test
    fun testBooking() {
        val config = AirlineConfig(
            audioAlertsInterval = 1.seconds,
            displayUpdateInterval = 100.milliseconds,
            ticketSaleEndTime = 1.hours - 2.seconds,
        )
        val airlineApplication = AirlineApplication(config, emailService)
        val plane1 = Plane("A312", setOf("1A", "1B", "2A", "2B"))
        val plane2 = Plane("B737", setOf("1A", "1B", "2A", "2B", "3A", "3B"))
        val flightId1 = "111"
        val flightId2 = "222"
        val flightTime = Clock.System.now() + 1.hours

        testAndCancel {
            launch { airlineApplication.run() }
            sleep()

            val booking = airlineApplication.bookingService
            val management = airlineApplication.managementService

            management.scheduleFlight(flightId1, flightTime, plane1)
            management.scheduleFlight(flightId2, flightTime, plane2)
            sleep()

            assertContains(booking.flightSchedule, FlightShortInfo(flightId1, flightTime))
            assertContains(booking.flightSchedule, FlightShortInfo(flightId2, flightTime))

            booking.buyTicket(flightId1, flightTime, "2B", "1", "Konstantin Bats", "kbats@itmo.ru")
            sleep()

            checkEmails("kbats@itmo.ru", flightId1, "2B", "successfully bought")
            Assertions.assertEquals(setOf("1A", "1B", "2A"), booking.freeSeats(flightId1, flightTime))

            // buy ticket on same place
            booking.buyTicket(flightId1, flightTime, "2B", "1", "Petrov Petr", "test@example.com")
            sleep()

            /* Here and below you can replace `Failed to buy` to any other text */
            checkEmails("test@example.com", flightId1, "2B", "Failed to buy")
            Assertions.assertEquals(setOf("1A", "1B", "2A"), booking.freeSeats(flightId1, flightTime))

            // buy ticket on not existing place
            booking.buyTicket(flightId1, flightTime, "3A", "1", "Konstantin Bats", "kbats@itmo.ru")
            sleep()

            checkEmails("kbats@itmo.ru", flightId1, "Failed to buy")
            Assertions.assertEquals(setOf("1A", "1B", "2A"), booking.freeSeats(flightId1, flightTime))

            // buy ticket on existing place in other plane
            booking.buyTicket(flightId2, flightTime, "3A", "1", "Konstantin Bats", "kbats@itmo.ru")
            sleep()

            checkEmails("kbats@itmo.ru", flightId2, "successfully bought")
            Assertions.assertEquals(setOf("1A", "1B", "2A", "2B", "3B"), booking.freeSeats(flightId2, flightTime))

            management.cancelFlight(flightId2, flightTime)
            // buy ticket on cancelled flight
            booking.buyTicket(flightId2, flightTime, "3B", "2", "Any Person", "test@hello.com")
            sleep()
            checkEmails("test@hello.com", "Failed to buy")

            // await to closing booking
            sleep(3.seconds)
            Assertions.assertEquals(0, booking.flightSchedule.size)

            // buy ticket after booking closing
            booking.buyTicket(flightId1, flightTime, "1B", "2", "Ivan Petrov", "ipetrov@mail.ru")
            sleep()
            checkEmails("ipetrov@mail.ru", "Failed to buy")

            // check, that we still can watch free seats
            // check, that we can't book ticket after closing booking
            Assertions.assertEquals(0, booking.flightSchedule.size)
            Assertions.assertEquals(setOf("1A", "1B", "2A"), booking.freeSeats(flightId1, flightTime))
        }
    }

    @Test
    fun testAudioAlerts() {
        val config = AirlineConfig(
            audioAlertsInterval = 1.seconds,
            registrationOpeningTime = 3.hours,
            registrationClosingTime = 40.minutes,
            boardingOpeningTime = 35.minutes,
            boardingClosingTime = 20.minutes,
        )
        val airlineApplication = AirlineApplication(config, emailService)

        val plane = Plane("A312", setOf("1A", "1B", "2A", "2B"))
        val flightId = "111"
        val flightTime = Clock.System.now() + config.registrationOpeningTime
        val flightId2 = "222"
        val flightTime2 = Clock.System.now() + config.registrationClosingTime + 3.minutes
        val flightId3 = "333"
        val flightTime3 = Clock.System.now() + config.boardingOpeningTime
        val flightId4 = "444"
        val flightTime4 = Clock.System.now() + config.boardingClosingTime + 3.minutes

        testAndCancel {
            launch { airlineApplication.run() }
            sleep()

            val management = airlineApplication.managementService
            val audioAlerts = airlineApplication.airportAudioAlerts

            management.scheduleFlight(flightId, flightTime, plane)
            management.scheduleFlight(flightId2, flightTime2, plane)
            management.scheduleFlight(flightId3, flightTime3, plane)
            management.scheduleFlight(flightId4, flightTime4, plane)
            management.setCheckInNumber(flightId, flightTime, "checkin1")
            management.setCheckInNumber(flightId2, flightTime2, "checkin2")
            management.setGateNumber(flightId3, flightTime3, "gate3")
            management.setGateNumber(flightId4, flightTime4, "gate4")

            sleep()

            val previousEvent = mutableMapOf<String, Instant>()
            fun assertEventsInterval(flightId: String) {
                val now = Clock.System.now()
                previousEvent[flightId]?.let { p ->
                    Assertions.assertTrue(now - p in 1.seconds..2.seconds)
                }
                previousEvent[flightId] = now
            }

            audioAlerts.take(12).collect {
                when (it) {
                    is AudioAlerts.RegistrationOpen -> {
                        Assertions.assertEquals("111", it.flightNumber)
                        Assertions.assertEquals("checkin1", it.checkInNumber)
                        assertEventsInterval(it.flightNumber)
                    }

                    is AudioAlerts.RegistrationClosing -> {
                        Assertions.assertEquals("222", it.flightNumber)
                        Assertions.assertEquals("checkin2", it.checkInNumber)
                        assertEventsInterval(it.flightNumber)
                    }

                    is AudioAlerts.BoardingOpened -> {
                        Assertions.assertEquals("333", it.flightNumber)
                        Assertions.assertEquals("gate3", it.gateNumber)
                        assertEventsInterval(it.flightNumber)
                    }

                    is AudioAlerts.BoardingClosing -> {
                        Assertions.assertEquals("444", it.flightNumber)
                        Assertions.assertEquals("gate4", it.gateNumber)
                        assertEventsInterval(it.flightNumber)
                    }
                }
            }
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun testInformationDisplay() {
        val config = AirlineConfig(
            displayUpdateInterval = 100.milliseconds,
        )
        val airlineApplication = AirlineApplication(config, emailService)
        val plane1 = Plane("B737", setOf("1A", "1B", "2A", "2B"))
        val flightId = "111"
        val flightTime = Clock.System.now() + 1.hours

        testAndCancel {
            launch { airlineApplication.run() }
            sleep()

            val booking = airlineApplication.bookingService
            val management = airlineApplication.managementService
            val display = airlineApplication.airportInformationDisplay(this)

            management.scheduleFlight(flightId, flightTime, plane1)
            sleep(200.milliseconds)
            assertContains(display, FlightShortInfo(flightId, flightTime))

            management.delayFlight(flightId, flightTime, flightTime + 1.minutes)
            sleep(200.milliseconds)
            assertContains(
                display,
                FlightShortInfo(flightId, flightTime, actualDepartureTime = flightTime + 1.minutes),
                actualTime = true,
            )

            management.setCheckInNumber(flightId, flightTime, "checkin")
            sleep(200.milliseconds)
            assertContains(
                display,
                FlightShortInfo(
                    flightId,
                    flightTime,
                    checkInNumber = "checkin",
                    actualDepartureTime = flightTime + 1.minutes,
                ),
                actualTime = true,
                checkIn = true,
            )

            management.setGateNumber(flightId, flightTime, "gate")
            sleep(200.milliseconds)
            assertContains(
                display,
                FlightShortInfo(
                    flightId,
                    flightTime,
                    checkInNumber = "checkin",
                    gateNumber = "gate",
                    actualDepartureTime = flightTime + 1.minutes,
                ),
                actualTime = true,
                checkIn = true,
                gate = true,
            )

            val displaySnapshotDeffered = CompletableDeferred<List<Pair<Instant, InformationDisplay>>>()

            launch(Dispatchers.Default) {
                val snapshots = mutableListOf<Pair<Instant, InformationDisplay>>()
                merge(display, ticker(1000).receiveAsFlow()).takeWhile { it !is Unit }.collect {
                    if (it is InformationDisplay) snapshots.add(Clock.System.now() to it)
                }
                displaySnapshotDeffered.complete(snapshots)
            }
            sleep(200.milliseconds)

            management.setGateNumber(flightId, flightTime, "gate2")
            management.setGateNumber(flightId, flightTime, "gate3")
            sleep()

            booking.buyTicket(flightId, flightTime, "1A", "111", "Konstantin", "kbats@itmo.ru")
            booking.buyTicket(flightId, flightTime, "2A", "111", "Konstantin", "kbats@itmo.ru")
            booking.buyTicket(flightId, flightTime, "1B", "111", "Konstantin", "kbats@itmo.ru")
            booking.buyTicket(flightId, flightTime, "2B", "111", "Konstantin", "kbats@itmo.ru")
            sleep(200.milliseconds)

            management.delayFlight(flightId, flightTime, flightTime + 20.minutes)

            val displaySnapshot = displaySnapshotDeffered.await()
            /* Here expected 3 state of display */
            /* 1. Before all modifications */
            /* 2. After gate changing */
            /* 3. After delaying */

            Assertions.assertEquals(3, displaySnapshot.size)

            Assertions.assertTrue(displaySnapshot[0].second.departing[0].gateNumber in setOf("gate"))
            Assertions.assertTrue(displaySnapshot[1].second.departing[0].gateNumber in setOf("gate2", "gate3"))
            Assertions.assertEquals(flightTime + 20.minutes, displaySnapshot[2].second.departing[0].actualDepartureTime)

            Assertions.assertTrue(displaySnapshot[1].first - displaySnapshot[0].first >= config.displayUpdateInterval)
            Assertions.assertTrue(displaySnapshot[2].first - displaySnapshot[1].first >= config.displayUpdateInterval)
        }
    }

    @Test
    fun testMultipleFlights() {
        val config = AirlineConfig(
            audioAlertsInterval = 1.seconds,
            displayUpdateInterval = 10.milliseconds,
            ticketSaleEndTime = 1.hours - 1.minutes,
            registrationOpeningTime = 20.seconds,
            registrationClosingTime = 1.seconds,
            boardingOpeningTime = 20.seconds,
            boardingClosingTime = 0.5.seconds,
        )
        val airlineApplication = AirlineApplication(config, emailService)
        val plane1 = Plane("A312", setOf("1A", "1B", "2A", "2B"))
        val plane2 = Plane("B747", setOf("1A", "1B", "2A", "2B", "3A", "3B"))
        val plane3 = Plane("C666", setOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "5A"))
        val flightId1 = "111"
        val flightTime1 = Clock.System.now() + 1.hours
        val flightId2 = "222"
        val flightTime2 = Clock.System.now() + 3.hours
        val flightId3 = "333"
        val flightTime3 = Clock.System.now() + 23.seconds

        testAndCancel {
            launch { airlineApplication.run() }
            sleep()

            val booking = airlineApplication.bookingService
            val management = airlineApplication.managementService
            val display = airlineApplication.airportInformationDisplay(this)

            management.scheduleFlight(flightId1, flightTime1, plane1)
            management.scheduleFlight(flightId2, flightTime2, plane2)
            management.scheduleFlight(flightId3, flightTime3, plane3)
            sleep()

            Assertions.assertEquals(
                setOf(
                    FlightShortInfo(flightId1, flightTime1),
                    FlightShortInfo(flightId2, flightTime2),
                    FlightShortInfo(flightId3, flightTime3),
                ),
                display.value.departing.short(),
            )

            sleep(5.seconds)

            Assertions.assertEquals(
                setOf(
                    FlightShortInfo(flightId1, flightTime1),
                    FlightShortInfo(flightId2, flightTime2),
                ),
                booking.flightSchedule.short(),
            )

            sleep()

            booking.buyTicket(flightId1, flightTime1, "1A", "1", "Konstantin Bats", "kbats@itmo.ru")
            booking.buyTicket(flightId2, flightTime2, "1A", "1", "Ivan Petrov", "ipetrov@mail.ru")
            sleep(200.milliseconds)
            checkEmails("kbats@itmo.ru", flightId1, "1A")
            checkEmails("ipetrov@mail.ru", flightId2, "1A")

            Assertions.assertEquals(booking.freeSeats(flightId1, flightTime1), setOf("1B", "2A", "2B"))
            Assertions.assertEquals(booking.freeSeats(flightId2, flightTime2), setOf("1B", "2A", "2B", "3A", "3B"))

            Assertions.assertEquals(3, display.value.departing.size)
            management.setCheckInNumber(flightId1, flightTime1, "checkin1")
            management.setCheckInNumber(flightId2, flightTime2, "checkin2")
            management.setCheckInNumber(flightId3, flightTime3, "checkin3")

            checkEmails("kbats@itmo.ru", "checkin1")
            checkEmails("ipetrov@mail.ru", "checkin2")
            sleep()

            Assertions.assertEquals(3, display.value.departing.size)
            Assertions.assertEquals(flightId1, display.value.departing[0].flightId)
            Assertions.assertEquals("checkin1", display.value.departing[0].checkInNumber)
            Assertions.assertEquals(flightId2, display.value.departing[1].flightId)
            Assertions.assertEquals("checkin2", display.value.departing[1].checkInNumber)

            management.delayFlight(flightId1, flightTime1, flightTime1 + 30.minutes)
            management.delayFlight(flightId2, flightTime2, flightTime2 + 2.hours)
            sleep()

            checkEmails("kbats@itmo.ru", "111", "delayed")
            checkEmails("ipetrov@mail.ru", "222", "delayed")

            Assertions.assertEquals(
                setOf(
                    FlightShortInfo(flightId1, flightTime1, "checkin1", null, flightTime1 + 30.minutes),
                    FlightShortInfo(flightId2, flightTime2, "checkin2", null, flightTime2 + 2.hours),
                    FlightShortInfo(flightId3, flightTime3, "checkin3", null, flightTime3),
                ),
                display.value.departing.short(checkIn = true, actualTime = true),
            )

            management.cancelFlight(flightId1, flightTime1)

            sleep()

            checkEmails("kbats@itmo.ru", "111", "cancelled")
            assertContains(display, FlightShortInfo(flightId1, flightTime1, isCancelled = true), cancellation = true)

            management.setGateNumber(flightId3, flightTime3, "gate3")

            val audio = airlineApplication.airportAudioAlerts
            val counter = mutableSetOf<AudioAlerts>()
            audio.take(4).collect { notification -> counter.add(notification) }
            Assertions.assertTrue(AudioAlerts.RegistrationOpen(flightId3, "checkin3") in counter)
            Assertions.assertTrue(AudioAlerts.RegistrationClosing(flightId3, "checkin3") in counter)
            Assertions.assertTrue(AudioAlerts.BoardingOpened(flightId3, "gate3") in counter)
            Assertions.assertTrue(AudioAlerts.BoardingClosing(flightId3, "gate3") in counter)
        }
    }

    private fun testAndCancel(block: suspend CoroutineScope.() -> Unit) {
        try {
            runBlocking(Dispatchers.Default) {
                block()
                cancel()
            }
        } catch (ignore: CancellationException) {
        }
    }

    private suspend fun sleep(interval: Duration = 50.milliseconds) {
        delay(interval)
    }

    private data class FlightShortInfo(
        val flightId: String,
        val departureTime: Instant,
        val checkInNumber: String? = null,
        val gateNumber: String? = null,
        val actualDepartureTime: Instant? = null,
        val isCancelled: Boolean? = null,
    )

    private fun List<FlightInfo>.short(
        checkIn: Boolean = false,
        gate: Boolean = false,
        actualTime: Boolean = false,
        cancellation: Boolean = false,
    ) = map {
        FlightShortInfo(
            it.flightId,
            it.departureTime,
            it.checkInNumber.takeIf { checkIn },
            it.gateNumber.takeIf { gate },
            it.actualDepartureTime.takeIf { actualTime },
            it.isCancelled.takeIf { cancellation },
        )
    }.toSet()

    private fun assertContains(
        flights: List<FlightInfo>,
        shortInfo: FlightShortInfo,
        checkIn: Boolean = false,
        gate: Boolean = false,
        actualTime: Boolean = false,
        cancellation: Boolean = false,
    ) {
        val actual = flights.short(checkIn, gate, actualTime, cancellation)
        Assertions.assertTrue(shortInfo in actual) {
            "expected: <$shortInfo> in display departing flights, but it actual <$actual>"
        }
    }

    private fun assertContains(
        display: StateFlow<InformationDisplay>,
        shortInfo: FlightShortInfo,
        checkIn: Boolean = false,
        gate: Boolean = false,
        actualTime: Boolean = false,
        cancellation: Boolean = false,
    ) {
        assertContains(display.value.departing, shortInfo, checkIn, gate, actualTime, cancellation)
    }

    private class InChannelEmailService : EmailService {
        val messages = ConcurrentHashMap<String, Channel<String>>()

        override suspend fun send(to: String, text: String) {
            messages.computeIfAbsent(to) { Channel(100) }
            messages[to]?.send(text)
        }
    }

    private var emailService = InChannelEmailService()

    @BeforeEach
    fun initEmailService() {
        emailService = InChannelEmailService()
    }

    private suspend fun checkEmails(email: String, vararg text: String) {
        val serviceText = emailService.messages[email]?.receive() ?: Assertions.fail("No such email for $email")
        for (part in text) {
            Assertions.assertTrue(part in serviceText) { "expected <$part> in <$serviceText> of email message" }
        }
    }
}
