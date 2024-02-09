package airline

import airline.api.*
import airline.servies.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AirlineApplication(val config: AirlineConfig, emailService: EmailService) {

    private val state = MutableStateFlow(emptyList<Flight>())

    private val events = MutableSharedFlow<Event>()

    private val bufferedEmailService = BufferedEmailService(emailService)

    private val passengerNotificationService = PassengerNotificationService(emailService)

    private val AUDIOPERIOD = 3.minutes

    val bookingService: BookingServices = object : BookingServices {
        override val flightSchedule: List<FlightInfo>
            get() = state.value.filter {
                it.tickers.size != it.plane.seats.size &&
                    it.departureTime > Clock.System.now() + config.ticketSaleEndTime
            }.map {
                it.toFlightInfo()
            }

        override fun freeSeats(flightId: String, departureTime: Instant): Set<String> {
            val currFlight = state.value.first {
                it.flightId == flightId && it.departureTime == departureTime
            }
            return currFlight.plane.seats.filter { !currFlight.tickers.containsKey(it) }.toSet()
        }

        override suspend fun buyTicket(
            flightId: String,
            departureTime: Instant,
            seatNo: String,
            passengerId: String,
            passengerName: String,
            passengerEmail: String,
        ) {
            events.emit(
                BuyTicket(flightId, departureTime, seatNo, passengerId, passengerName, passengerEmail),
            )
        }

    }

    val managementService: AirlineManagementService = object : AirlineManagementService {
        override suspend fun scheduleFlight(flightId: String, departureTime: Instant, plane: Plane) {
            events.emit(SF(flightId, departureTime, plane))
        }

        override suspend fun delayFlight(flightId: String, departureTime: Instant, actualDepartureTime: Instant) {
            events.emit(DF(flightId, departureTime, actualDepartureTime))
        }

        override suspend fun cancelFlight(flightId: String, departureTime: Instant) {
            events.emit(CF(flightId, departureTime))
        }

        override suspend fun setCheckInNumber(flightId: String, departureTime: Instant, checkInNumber: String) {
            events.emit(SetCN(flightId, departureTime, checkInNumber))
        }

        override suspend fun setGateNumber(flightId: String, departureTime: Instant, gateNumber: String) {
            events.emit(SetGN(flightId, departureTime, gateNumber))
        }

    }

    @OptIn(FlowPreview::class)
    fun airportInformationDisplay(coroutineScope: CoroutineScope): StateFlow<InformationDisplay> {
        return state
            .sample(config.displayUpdateInterval)
            .map { flights ->
                val departing =
                    flights.filter {
                        it.departureTime > Clock.System.now() && it.departureTime < Clock.System.now() + 1.days
                    }.map {
                        it.toFlightInfo()
                    }
                InformationDisplay(departing)
            }
            .distinctUntilChanged()
            .stateIn(coroutineScope, SharingStarted.Eagerly, InformationDisplay(emptyList()))
    }

    val airportAudioAlerts: Flow<AudioAlerts> = flow {
        while (true) {
            state.value.forEach {
                val registrationOpenTime = it.actualDepartureTime - config.registrationOpeningTime
                val registrationClosingTime = it.actualDepartureTime - config.registrationClosingTime
                val boardingOpenTime = it.actualDepartureTime - config.boardingOpeningTime
                val boardingClosingTime = it.actualDepartureTime - config.boardingClosingTime

                if (Clock.System.now() in registrationOpenTime..registrationOpenTime + AUDIOPERIOD &&
                    it.checkInNumber != null
                ) {
                    emit(AudioAlerts.RegistrationOpen(it.flightId, it.checkInNumber))
                }
                if (Clock.System.now() in registrationClosingTime - AUDIOPERIOD..registrationClosingTime &&
                    it.checkInNumber != null
                ) {
                    emit(AudioAlerts.RegistrationClosing(it.flightId, it.checkInNumber))
                }
                if (Clock.System.now() in boardingOpenTime..boardingOpenTime + AUDIOPERIOD &&
                    it.gateNumber != null
                ) {
                    emit(AudioAlerts.BoardingOpened(it.flightId, it.gateNumber))
                }
                if (Clock.System.now() in boardingClosingTime - AUDIOPERIOD..boardingClosingTime &&
                    it.gateNumber != null
                ) {
                    emit(AudioAlerts.BoardingClosing(it.flightId, it.gateNumber))
                }
            }
            delay(config.audioAlertsInterval)
        }
    }

    suspend fun run() {
        coroutineScope {
            launch {
                bufferedEmailService.run()
            }
            launch {
                passengerNotificationService.run()
            }

            events.collect { event ->
                when (event) {
                    is SF -> {
                        if (state.value.firstOrNull {
                                it.flightId == event.flightId && it.departureTime == event.departureTime
                            } == null
                        ) {
                            state.value += Flight(
                                flightId = event.flightId,
                                departureTime = event.departureTime,
                                plane = event.plane,
                            )
                        }
                    }

                    is DF -> {
                        val curFlight = state.value.firstOrNull {
                            it.flightId == event.flightId && it.departureTime == event.departureTime
                        }
                        if (curFlight != null) {
                            val newFlight = curFlight.copy(actualDepartureTime = event.actualDepartureTime)
                            state.value = state.value - curFlight + newFlight
                            passengerNotificationService.sendNotification(DelayFlight(newFlight))
                        }
                    }

                    is CF -> {
                        val curFlight =
                            state.value.firstOrNull {
                                it.flightId == event.flightId && it.departureTime == event.departureTime
                            }
                        if (curFlight != null) {
                            val newFlight = curFlight.copy(isCancelled = true)
                            state.value = state.value - curFlight + newFlight
                            passengerNotificationService.sendNotification(CancelFlight(newFlight))
                        }
                    }

                    is SetCN -> {
                        val curFlight =
                            state.value.firstOrNull {
                                it.flightId == event.flightId && it.departureTime == event.departureTime
                            }
                        if (curFlight != null) {
                            val newFlight = curFlight.copy(checkInNumber = event.checkInNumber)
                            state.value = state.value - curFlight + newFlight
                            passengerNotificationService.sendNotification(CheckIn(newFlight))
                        }
                    }

                    is SetGN -> {
                        val curFlight =
                            state.value.firstOrNull {
                                it.flightId == event.flightId && it.departureTime == event.departureTime
                            }
                        if (curFlight != null) {
                            val newFlight = curFlight.copy(gateNumber = event.gateNumber)
                            state.value = state.value - curFlight + newFlight
                            passengerNotificationService.sendNotification(Gate(newFlight))
                        }
                    }

                    is BuyTicket -> {
                        val curFlight =
                            state.value.firstOrNull {
                                it.flightId == event.flightId && it.departureTime == event.departureTime
                            }
                        if (curFlight == null ||
                            event.seatNo in curFlight.tickers.keys ||
                            event.seatNo !in curFlight.plane.seats ||
                            curFlight.isCancelled ||
                            curFlight.toFlightInfo() !in bookingService.flightSchedule
                        ) {
                            bufferedEmailService.sendNotification(
                                UnsucTicket(
                                    event.passengerEmail,
                                    event.passengerName,
                                    event.flightId,
                                    event.seatNo,
                                ),
                            )
                        } else {
                            val ticket = Ticket(
                                event.flightId,
                                event.departureTime,
                                event.seatNo,
                                event.passengerId,
                                event.passengerName,
                                event.passengerEmail,
                            )
                            val newTickers = curFlight.tickers
                            newTickers[event.seatNo] = ticket
                            val newFlight = curFlight.copy(tickers = newTickers)
                            state.value = state.value - curFlight + newFlight
                            bufferedEmailService.sendNotification(
                                SucTicket(
                                    event.passengerEmail,
                                    event.passengerName,
                                    event.flightId,
                                    event.seatNo,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
