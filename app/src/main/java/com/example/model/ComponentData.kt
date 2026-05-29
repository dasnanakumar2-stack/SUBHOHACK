package com.example.model

data class PinInfo(
    val number: Int,
    val name: String,
    val description: String
)

data class Component(
    val id: String,
    val name: String,
    val category: String,
    val subcategory: String,
    val packageType: String,
    val typicalApplication: String,
    val description: String,
    val pinouts: List<PinInfo>,
    val datasheetUrl: String,
    val internalSymbolType: String // e.g. "RESISTOR", "CAPACITOR", "TRANSISTOR_BJT", "MOSFET_N", "DIODE", "IC_8", "IC_3", "INDUCTOR", "SWITCH"
)

object ComponentDatabase {
    val categories = listOf(
        "Resistors",
        "Capacitors",
        "Transistors & MOSFETs",
        "Diodes",
        "Integrated Circuits",
        "Inductors & Transformers",
        "Switches & Relays"
    )

    val components = listOf(
        // === RESISTORS ===
        Component(
            id = "res_axial_carbon",
            name = "Axial Lead Carbon Film Resistor",
            category = "Resistors",
            subcategory = "Axial",
            packageType = "Axial Through-hole (1/4W)",
            typicalApplication = "Current limiting, voltage division, pull-up/pull-down networks.",
            description = "A standard carbon film resistor. Consists of a ceramic carrier with a thin layer of carbon deposited on it, spiraled to achieve accurate resistance value.",
            pinouts = listOf(
                PinInfo(1, "Terminal A", "Anode/Cathode non-polarized lead"),
                PinInfo(2, "Terminal B", "Anode/Cathode non-polarized lead")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=CARBON+FILM+RESISTOR",
            internalSymbolType = "RESISTOR"
        ),
        Component(
            id = "res_smd_0603",
            name = "Thick Film SMD Resistor (0603)",
            category = "Resistors",
            subcategory = "SMD Resistors",
            packageType = "SMD 0603",
            typicalApplication = "Ultra-compact circuits, digital logic designs, smartphone accessories.",
            description = "Surface mount device (SMD) resistor. It measures 1.6mm x 0.8mm (0.06 x 0.03 in). Popular for pick-and-place high-density PCB manufacturing.",
            pinouts = listOf(
                PinInfo(1, "Terminal 1", "Solder end terminal 1"),
                PinInfo(2, "Terminal 2", "Solder end terminal 2")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=SMD+0603+RESISTOR",
            internalSymbolType = "SMD_RESISTOR"
        ),
        Component(
            id = "res_smd_1206",
            name = "Thick Film SMD Resistor (1206)",
            category = "Resistors",
            subcategory = "SMD Resistors",
            packageType = "SMD 1206",
            typicalApplication = "Power supply feedback, standard logic, small analog filters.",
            description = "Slightly larger SMD resistor (3.2mm x 1.6mm) offering a higher power capability (usually 1/4W) compared to smaller SMD variants.",
            pinouts = listOf(
                PinInfo(1, "Terminal 1", "Solder pad 1"),
                PinInfo(2, "Terminal 2", "Solder pad 2")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=SMD+1206+RESISTOR",
            internalSymbolType = "SMD_RESISTOR"
        ),
        Component(
            id = "res_pot_rotary",
            name = "Rotary Carbon Potentiometer",
            category = "Resistors",
            subcategory = "Potentiometers",
            packageType = "3-Pin Through-hole Panel Mount",
            typicalApplication = "User interfaces, audio volume control, analog sensor tuning.",
            description = "An adjustable three-terminal resistor. Moving the wiper creates a variable voltage divider relative to the input terminal voltages.",
            pinouts = listOf(
                PinInfo(1, "Terminal 1", "Outer terminal (Low potential references)"),
                PinInfo(2, "Wiper", "Adjustable contact terminal output"),
                PinInfo(3, "Terminal 2", "Outer terminal (High potential references)")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=ROTARY+POTENTIOMETER",
            internalSymbolType = "POTENTIOMETER"
        ),
        Component(
            id = "res_network_sip",
            name = "SIP Resistor Network",
            category = "Resistors",
            subcategory = "Network Resistors",
            packageType = "SIP-9 Through-hole",
            typicalApplication = "Common bus pull-ups for microcontrollers, multiple matching LED limiters.",
            description = "A Single In-line Package (SIP) containing 8 matched isolated or bussed resistors connected to a common node (Pin 1). Save space on PCBs.",
            pinouts = listOf(
                PinInfo(1, "COM", "Common entry pin connected to all internal resistors"),
                PinInfo(2, "R1", "Terminal of Resistor 1"),
                PinInfo(3, "R2", "Terminal of Resistor 2"),
                PinInfo(4, "R3", "Terminal of Resistor 3"),
                PinInfo(5, "R4", "Terminal of Resistor 4"),
                PinInfo(6, "R5", "Terminal of Resistor 5"),
                PinInfo(7, "R6", "Terminal of Resistor 6"),
                PinInfo(8, "R7", "Terminal of Resistor 7"),
                PinInfo(9, "R8", "Terminal of Resistor 8")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=SIP+RESISTOR+NETWORK",
            internalSymbolType = "IC_8"
        ),

        // === CAPACITORS ===
        Component(
            id = "cap_electrolytic",
            name = "Aluminum Electrolytic Capacitor",
            category = "Capacitors",
            subcategory = "Electrolytic",
            packageType = "Radial Cylindrical Through-hole",
            typicalApplication = "Power supply input decoupling, DC filtering, low-frequency coupling.",
            description = "Polarized capacitor with high capacitance value per volume. Utilizes an liquid or gel electrolyte to achieve large surface area oxide layers.",
            pinouts = listOf(
                PinInfo(1, "+ (Anode)", "Positive terminal (Longer lead)"),
                PinInfo(2, "- (Cathode)", "Negative terminal (Shorter lead, highlighted with strip on body)")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=ELECTROLYTIC+CAPACITOR+100UF",
            internalSymbolType = "CAP_POLAR"
        ),
        Component(
            id = "cap_ceramic_disc",
            name = "Ceramic Disc Capacitor",
            category = "Capacitors",
            subcategory = "Ceramic Disc",
            packageType = "Radial Disc Through-hole",
            typicalApplication = "High-frequency noise filtering, decoupling, RF transceiver matching.",
            description = "Non-polarized capacitor composed of two metal plates sandwiching a ceramic dielectric layer. Stable at very high frequencies.",
            pinouts = listOf(
                PinInfo(1, "Pin 1", "Non-polarized terminal 1"),
                PinInfo(2, "Pin 2", "Non-polarized terminal 2")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=CERAMIC+CAPACITOR+100NF",
            internalSymbolType = "CAPACITOR"
        ),
        Component(
            id = "cap_smd_mlcc",
            name = "SMD Multi-layer Ceramic (MLCC)",
            category = "Capacitors",
            subcategory = "SMD MLCC",
            packageType = "SMD 0805",
            typicalApplication = "High-speed decoupling close to IC power pins, bypass filters.",
            description = "Solder pad multi-layer ceramic capacitor. Formed of interleaved monolithic ceramic dielectric layers. Small size, ultra-low ESR.",
            pinouts = listOf(
                PinInfo(1, "Terminal 1", "End terminal 1"),
                PinInfo(2, "Terminal 2", "End terminal 2")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=SMD+MLCC+0805",
            internalSymbolType = "SMD_CAP"
        ),
        Component(
            id = "cap_tantalum",
            name = "Sanded Tantalum Capacitor",
            category = "Capacitors",
            subcategory = "Tantalum",
            packageType = "SMD Case A (3216)",
            typicalApplication = "Sleek low-profile power regulators, avionics, high reliability filters.",
            description = "Polarized electrolytic capacitor. Delivers high capacitance stability over temperature, extremely low leakage currents, and excellent life expectancy.",
            pinouts = listOf(
                PinInfo(1, "+ (Anode)", "Positive terminal (Marked with bevel stripe)"),
                PinInfo(2, "- (Cathode)", "Negative terminal")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=SMD+TANTALUM+CAPACITOR",
            internalSymbolType = "SMD_CAP_POLAR"
        ),

        // === TRANSISTORS & MOSFETS ===
        Component(
            id = "trans_bjt_bc547",
            name = "BC547 NPN Bipolar Transistor",
            category = "Transistors & MOSFETs",
            subcategory = "BJT",
            packageType = "TO-92",
            typicalApplication = "Small signal switching, audio pre-amplifiers, logic gate drivers.",
            description = "A standard silicon NPN bipolar junction transistor. Ideal for low-power amplifying and fast threshold electronics switching.",
            pinouts = listOf(
                PinInfo(1, "Collector", "Input current terminal (Positive relative to emitter)"),
                PinInfo(2, "Base", "Control pin; a small current opens junction"),
                PinInfo(3, "Emitter", "Output current drain pin")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=BC547",
            internalSymbolType = "TRANSISTOR_BJT"
        ),
        Component(
            id = "trans_mos_irf540n",
            name = "IRF540N N-Channel Power MOSFET",
            category = "Transistors & MOSFETs",
            subcategory = "N-channel MOSFETs",
            packageType = "TO-220",
            typicalApplication = "High-speed PWM motor control, solenoid drivers, battery switch circuits.",
            description = "Robust power N-Channel Metal-Oxide-Semiconductor Field-Effect Transistor. Handles up to 33A and 100V with very low RDS(on) resistance.",
            pinouts = listOf(
                PinInfo(1, "Gate", "Voltage control terminal (Triggers main conduction)"),
                PinInfo(2, "Drain", "Output load terminal connected to center tab"),
                PinInfo(3, "Source", "Ground or low-voltage reference terminal")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=IRF540N",
            internalSymbolType = "MOSFET_N"
        ),
        Component(
            id = "trans_mos_sot23_bss138",
            name = "BSS138 N-Channel SMD MOSFET",
            category = "Transistors & MOSFETs",
            subcategory = "SMD SOT-23/SOT-223",
            packageType = "SMD SOT-23",
            typicalApplication = "I2C level shifting, high-frequency digital logic gates, LED strip switches.",
            description = "An ultra-small surface-mount logic level converter MOSFET. Allows 3.3V logic platforms to interact with 5V sensor peripherals.",
            pinouts = listOf(
                PinInfo(1, "Gate", "Trigger control input"),
                PinInfo(2, "Source", "Connected to logic ground"),
                PinInfo(3, "Drain", "Load drive pull-down collector")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=BSS138",
            internalSymbolType = "SOT23"
        ),

        // === DIODES ===
        Component(
            id = "diode_1n4007",
            name = "1N4007 Rectifier Diode",
            category = "Diodes",
            subcategory = "Rectifier",
            packageType = "DO-41 Axial Lead",
            typicalApplication = "AC to DC bridge rectification, inductive voltage surge clamping (flyback).",
            description = "Highly reliable ironclad silicon power rectifier diode. Designed to pass up to 1A with reverse peak breakdown rated up to 1000V.",
            pinouts = listOf(
                PinInfo(1, "Anode", "Positive current entry"),
                PinInfo(2, "Cathode", "Negative block terminal (Marked with silver stripe ring)")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=1N4007",
            internalSymbolType = "DIODE"
        ),
        Component(
            id = "diode_led_5mm",
            name = "5mm Diffused Red LED",
            category = "Diodes",
            subcategory = "LED",
            packageType = "Radial Through-hole LED",
            typicalApplication = "Status indicator lights, custom displays, optical triggers.",
            description = "A standard light emitting diode. Converts electronic current directly into cold photons. Forward voltage is typically 1.8V - 2.2V for red color.",
            pinouts = listOf(
                PinInfo(1, "Anode (+)", "Positive lead (Longer leg on package)"),
                PinInfo(2, "Cathode (-)", "Negative lead (Shorter leg, positioned next to flat notch on base rim)")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=5MM+LED+RED",
            internalSymbolType = "LED"
        ),

        // === INTEGRATED CIRCUITS ===
        Component(
            id = "ic_timer_555_dip",
            name = "NE555 Precision Timer (DIP-8)",
            category = "Integrated Circuits",
            subcategory = "Timers",
            packageType = "DIP-8",
            typicalApplication = "Monostable pulses, astable clock signaling, frequency dividers, PWM modulations.",
            description = "Legendary 8-pin precision timer IC. Can generate accurate interval delays, oscillation frequencies, and PWM outputs with high current drive capability.",
            pinouts = listOf(
                PinInfo(1, "GND", "Ground reference (0V)"),
                PinInfo(2, "TRIG", "Trigger pin (Starts timing cycle when voltage drops below 1/3 VCC)"),
                PinInfo(3, "OUT", "Pulse output pin (Can source or sink up to 200mA)"),
                PinInfo(4, "RESET", "Active-low reset override (Forces output low when grounded)"),
                PinInfo(5, "CONTROL", "Control voltage to modify threshold comparison values"),
                PinInfo(6, "THRESH", "Threshold timing comparator input (Ends cycle when voltage exceeds 2/3 VCC)"),
                PinInfo(7, "DISCHG", "Internal open-collector discharge path for capacitor circuit"),
                PinInfo(8, "VCC", "Supply voltage (+4.5V to +16V)")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=NE555",
            internalSymbolType = "IC_8"
        ),
        Component(
            id = "ic_timer_555_soic",
            name = "NE555 Precision Timer (SOIC-8)",
            category = "Integrated Circuits",
            subcategory = "Timers",
            packageType = "SOIC-8 SMD",
            typicalApplication = "Micro-size clock pulsers, high efficiency remote control, flight system sensors.",
            description = "The classic NE555 timer packaged in a compact surface-mount Small Outline integrated circuit configuration. Substantially reduces board scale.",
            pinouts = listOf(
                PinInfo(1, "GND", "Ground reference (0V)"),
                PinInfo(2, "TRIG", "Trigger pin input"),
                PinInfo(3, "OUT", "Amplifier output voltage"),
                PinInfo(4, "RESET", "Reset timing trigger"),
                PinInfo(5, "CONTROL", "Control threshold bias reference"),
                PinInfo(6, "THRESH", "Threshold detector input"),
                PinInfo(7, "DISCHG", "Capacitor discharge path"),
                PinInfo(8, "VCC", "Supply bias input (+5V)")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=NE555D",
            internalSymbolType = "SOIC_8"
        ),
        Component(
            id = "ic_opamp_lm358",
            name = "LM358 Dual Operational Amplifier",
            category = "Integrated Circuits",
            subcategory = "Op-Amps",
            packageType = "DIP-8 Dual Op-Amp",
            typicalApplication = "Sensor signal amplification, active filters, instrument frontends.",
            description = "Two independent op-amps housed in a single 8-pin chip. Compatible with single-supply systems, low current drain.",
            pinouts = listOf(
                PinInfo(1, "OUT A", "Output terminal of Amplifier A"),
                PinInfo(2, "IN- A", "Inverting input of Amplifier A"),
                PinInfo(3, "IN+ A", "Non-inverting input of Amplifier A"),
                PinInfo(4, "GND / V-", "Negative supply rail or ground reference"),
                PinInfo(5, "IN+ B", "Non-inverting input of Amplifier B"),
                PinInfo(6, "IN- B", "Inverting input of Amplifier B"),
                PinInfo(7, "OUT B", "Output terminal of Amplifier B"),
                PinInfo(8, "VCC / V+", "Positive supply rail input")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=LM358",
            internalSymbolType = "IC_8"
        ),
        Component(
            id = "ic_mcu_atmega328",
            name = "ATmega328P 8-Bit Microcontroller",
            category = "Integrated Circuits",
            subcategory = "Microcontrollers",
            packageType = "DIP-28 Through-hole",
            typicalApplication = "Arduino Uno prototyping core, generic embedded programming controllers.",
            description = "High perfomance AVR RISC microcontroller. Integrates 32KB ISP Flash memory, 1KB EEPROM, 2KB SRAM, 23 programmable I/O lines, and 10-bit ADC adapters.",
            pinouts = listOf(
                PinInfo(1, "RESET", "Hard reset toggle (Active low)"),
                PinInfo(2, "RXD (PD0)", "UART serial receive"),
                PinInfo(3, "TXD (PD1)", "UART serial transmit"),
                PinInfo(4, "INT0 (PD2)", "External interrupt trigger line 0"),
                PinInfo(5, "INT1 (PD3)", "External interrupt trigger line 1"),
                PinInfo(7, "VCC", "Core positive digital power"),
                PinInfo(8, "GND", "Core digital ground"),
                PinInfo(9, "XTAL1", "Inverting hand oscillator input"),
                PinInfo(10, "XTAL2", "Internal crystal pin 2"),
                PinInfo(22, "GND", "Analog core ground isolation reference"),
                PinInfo(27, "SDA (PC4)", "I2C standard serial data bus"),
                PinInfo(28, "SCL (PC5)", "I2C standard serial clock bus")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=ATMEGA328P",
            internalSymbolType = "IC_28"
        ),
        Component(
            id = "ic_reg_lm7805",
            name = "LM7805 5V Linear Voltage Regulator",
            category = "Integrated Circuits",
            subcategory = "Voltage Regulators",
            packageType = "TO-220 3-Pin Regulator",
            typicalApplication = "Stepping down unregulated 9V-24V DC to safe stable 5V for TTL chips and microcontrollers.",
            description = "Three-terminal monolithic linear voltage regulator. Features internal current limiting, thermal shutdown safeguards, and SOA protection.",
            pinouts = listOf(
                PinInfo(1, "INPUT", "Unregulated high DC voltage supply entry (7.5V to 25V)"),
                PinInfo(2, "GROUND", "Common path negative return point"),
                PinInfo(3, "OUTPUT", "Smooth standard 5.0V output line")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=LM7805",
            internalSymbolType = "REGULATOR"
        ),

        // === INDUCTORS & TRANSFORMERS ===
        Component(
            id = "ind_toroidal",
            name = "Toroidal Core Inductor",
            category = "Inductors & Transformers",
            subcategory = "Toroidal Inductors",
            packageType = "Through-hole Vertical Toroid",
            typicalApplication = "EMI shielding line filter, switching DC-DC buck step-down converter caches.",
            description = "Inductor featuring wire wrapped around an iron powder or ferrite ring. Closed magnetic field minimizes radiation loops.",
            pinouts = listOf(
                PinInfo(1, "Terminal A", "Coil starts terminal"),
                PinInfo(2, "Terminal B", "Coil ends terminal")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=TOROIDAL+INDUCTOR",
            internalSymbolType = "INDUCTOR"
        ),
        Component(
            id = "ind_smd_power",
            name = "SMD Wirewound Power Inductor",
            category = "Inductors & Transformers",
            subcategory = "SMD Inductors",
            packageType = "SMD Shielded Case",
            typicalApplication = "PWM power phase chokes, smartphone DC decoupling battery systems.",
            description = "Shielded high-power wirewound choke. Optimized for surface mounting, low active series resistance, high saturation current threshold.",
            pinouts = listOf(
                PinInfo(1, "Terminal 1", "Coiled conductor lead 1"),
                PinInfo(2, "Terminal 2", "Coiled conductor lead 2")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=SMD+POWER+INDUCTOR",
            internalSymbolType = "SMD_IND"
        ),

        // === SWITCHES, RELAYS, CONNECTORS ===
        Component(
            id = "sw_toggle",
            name = "Miniature SPDT Toggle Switch",
            category = "Switches & Relays",
            subcategory = "Switches",
            packageType = "Panel Solder Lugs",
            typicalApplication = "Power input selection, mode switching (A/B settings), physical bypass controls.",
            description = "Single Pole Double Throw (SPDT) toggle switch. Swaps the signal connection of the common center pin to either the left or right option terminal.",
            pinouts = listOf(
                PinInfo(1, "Option A", "Connects to COM when toggle lever is set right"),
                PinInfo(2, "COM (Common)", "Center terminal feed pivot"),
                PinInfo(3, "Option B", "Connects to COM when toggle lever is set left")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=SPDT+TOGGLE+SWITCH",
            internalSymbolType = "SWITCH"
        ),
        Component(
            id = "relay_spdt_5v",
            name = "5V SPDT Miniature Power Relay",
            category = "Switches & Relays",
            subcategory = "Relays",
            packageType = "5-Pin THT Relay Cuboid",
            typicalApplication = "Controlling high mains voltage appliances from safe low-power microcontroller outputs.",
            description = "An electromagnetic switch operated by an internal 5V supply wire coil. Offers galvanic isolation between control electronics and heavy AC/DC system loads.",
            pinouts = listOf(
                PinInfo(1, "Coil A", "Electromagnet control input terminal A (Triggered with 5V)"),
                PinInfo(2, "Coil B", "Electromagnet control input terminal B (Connected to ground)"),
                PinInfo(3, "COM", "Common pivot contact path"),
                PinInfo(4, "NC (Normally Closed)", "Connected to COM when relay coil is powered down (OFF)"),
                PinInfo(5, "NO (Normally Open)", "Connected to COM when relay coil is energized (ON)")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=5V+RELAY+SPDT",
            internalSymbolType = "RELAY"
        ),
        Component(
            id = "conn_usbc",
            name = "USB Type-C Female SMT Connector",
            category = "Switches & Relays",
            subcategory = "Connectors",
            packageType = "Hybrid SMD/THT 16-Pin",
            typicalApplication = "High speed charging power delivery, micro-controller flashing, high-frequency IO data transfer.",
            description = "Ultra high speed, reversible surface mount power and communications port designed according to the USB-C standard.",
            pinouts = listOf(
                PinInfo(1, "GND", "Digital and power common ground"),
                PinInfo(2, "VBUS", "Power delivery bus input line (up to 20V)"),
                PinInfo(3, "CC1", "Configuration Channel 1 for protocol negotiation"),
                PinInfo(4, "D+", "USB 2.0 Differential Data line (Positive)"),
                PinInfo(5, "D-", "USB 2.0 Differential Data line (Negative)"),
                PinInfo(6, "CC2", "Configuration Channel 2 for protocol negotiation")
            ),
            datasheetUrl = "https://www.alldatasheet.com/view.jsp?sSearch=USB-C+RECEPTACLE",
            internalSymbolType = "IC_8"
        )
    )
}
