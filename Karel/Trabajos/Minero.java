import kareltherobot.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//public class Minero extends Robot implements Directions
public class Minero extends AugmentedRobot implements Directions {
	// Object attributes

	private int tipoRobot;
	private int pasos;
	private int avenidaInicial;
	private int calleInicial;
	private int avenidaActual;
	private int calleActual;
	private int id;
	// Constants
	private static final int AVENIDA_ESPERA_EXT = 3;
	private static final int AVENIDA_ESPERA_TREN = 12;
	private static final int AVENIDA_INICIAL = 8;
	private static final int CALLE_EXTRACTOR = 16;
	private static final int CALLE_MINERO = 12;
	private static final int CALLE_TREN = 14;
	private static final int CALLE_ESPERA_EXT = 1;
	private static final int BEEPERS_POR_BODEGA = 3000;
	private static final int BEEPERS_EXTRACTOR = 50;
	private static final int BEEPERS_MINERO = 50;
	private static final int BEEPERS_TREN = 120;
	private static final int DEFAULT_ROBOTS = 2;
	private static final int NUMERO_BODEGAS = 4;
	private static final int TIPO_EXTRACTOR = 3;
	private static final int TIPO_MINERO = 1;
	private static final int TIPO_TREN = 2;
	private static final int VETA_AVENIDA = 13;
	private static final int VETA_CALLE = 11;
	private static final int VETA_ESPERA_AVENIDA = 14;
	private static final int VETA_ESPERA_CALLE = 10;
	// Static common robot data for the specific problem
	// Robot arrayList
	private static ArrayList<Object> objRobots;
	// Threads for run the robots
	private static ArrayList<Thread> objThreads;
	public static KarelDbClient db_client;
	// Booleans to control behaviors
	private static boolean minaVacia = false;
	private static boolean debugHabilitado = false;
	private static boolean ejecutarLog = false;
	private static boolean encontroVeta = false;
	private static boolean extraccionCompleta = false;
	private static boolean fondoVeta = false;
	private static boolean inicioExtraccion = false;
	private static boolean inicioTransporte = false;
	private static boolean salidaExtractores = false;
	private static boolean salidaTrenes = false;
	public static String db_name = "RobotDB";
	// Integers
	private static int bodegaEnUso;
	private static int[] arr_bodegas;
	private static int cantidadMineros = DEFAULT_ROBOTS;
	private static int cantidadTrenes = DEFAULT_ROBOTS;
	private static int cantidadExtractores = DEFAULT_ROBOTS;
	private static int minerosSalida = 0;
	private static int beepersExtraidos = 0;
	private static int trenesSalida = 0;
	private static Posiciones objPosiciones;
	// Semaphores
	private static Semaphore sem_extIngreso;
	private static Semaphore sem_extraccion;
	private static Semaphore sem_extractor;
	private static Semaphore sem_mineros;
	private static Semaphore sem_move;
	private static Semaphore sem_salida;
	private static Semaphore sem_trenes;
	private static Semaphore sem_trenInicioProceso;
	private static Semaphore sem_trenSalida;

	public static String convertirFormatoRobot(String entrada, int tipo) {

		Pattern pattern = Pattern.compile(
				"RobotID (\\d+) at \\(street: (\\d+)\\) \\(avenue: (\\d+)\\) \\(beepers: (\\d+)\\) \\( direction: (\\w+)\\) (on|off)");
		Matcher matcher = pattern.matcher(entrada);

		if (matcher.find()) {

			String idRobot = matcher.group(1);
			String calle = matcher.group(2);
			String avenida = matcher.group(3);
			String beepers = matcher.group(4);
			String direccion = matcher.group(5);
			boolean encendido = matcher.group(6).equals("on");

			String tipoRobot = "";
			switch (tipo) {
				case 1:
					tipoRobot = "Minero";
					break;
				case 2:
					tipoRobot = "Tren";
					break;
				case 3:
					tipoRobot = "Extractor";
					break;
				default:
					tipoRobot = "Desconocido";
			}

			String salida = "{\"tipoRobot\":\"" + tipoRobot + "\",\"encendido\":"
					+ encendido + ",\"idRobot\":" + idRobot + ",\"calle\":" + calle + ",\"avenida\":" + avenida
					+ ",\"beepers\":" + beepers
					+ ",\"direccion\":\"" + direccion + "\"}";
			return salida;
		} else {
			return "";
		}
	}

	public void pickBeeper() {
		super.pickBeeper();
		String stringPickbeeper = this.toString();
		convertirFormatoRobot(stringPickbeeper, tipoRobot);
	}

	public void turnLeft() {
		super.turnLeft();
		String stringTurnLeft = this.toString();
		convertirFormatoRobot(stringTurnLeft, tipoRobot);

	}

	@SuppressWarnings("unchecked")
	public void move(){
		super.move();
		JSONParser parser = new JSONParser();
		try {
			JSONObject jsonMove = (JSONObject) parser.parse(convertirFormatoRobot(this.toString(), tipoRobot));

			JSONObject newRecord = new JSONObject();

			newRecord.put("idRobot", jsonMove.get("idRobot"));
			newRecord.put("timeStamp", getActualDateTimeString());
			newRecord.put("encendido", jsonMove.get("encendido"));
			newRecord.put("avenida", jsonMove.get("avenida"));
			newRecord.put("calle", jsonMove.get("calle"));
			newRecord.put("beepers", jsonMove.get("beepers"));

			JSONObject recordUpdate = new JSONObject();
			JSONObject where = new JSONObject();


			recordUpdate.put("calle", jsonMove.get("calle"));
			recordUpdate.put("avenida", jsonMove.get("avenida"));

			where.put("idRobot", jsonMove.get("idRobot"));

			System.out.println(db_client.update(db_name, "Robot",recordUpdate ,where ));
			System.out.println(db_client.insert(db_name, "logEventos", newRecord));
		} catch (ParseException e) {
			System.out.println("Something went wrong while parsing: " + e.toString());	
		}




	}

	// Constructor for Minero class. Extends the AugmentedRobot and adds two
	// parameters:
	// tipo: indicates if the robot is TIPO_EXTRACTOR, TIPO_MINERO
	// id : number that unique identifies the robot. Don't use the RobotID attrib
	// from Parent classes.
	public Minero(int street, int avenue, Direction direction, int beeps, Color color, int tipo, int id) {
		// Invokes the parent and fill attributes
		super(street, avenue, direction, beeps, color);
		avenidaInicial = avenue;
		calleInicial = street;
		avenidaActual = avenue;
		calleActual = street;
		tipoRobot = tipo;
		this.id = id;
		// Register the current position and locks it indicating that is occupied.
		String posicion = Integer.toString(calleActual) + " - " + Integer.toString(avenidaActual);
		objPosiciones.ocuparPosicion(posicion);
		// Add robot thread to the World.
		World.setupThread(this);
	}

	// Runnable method that starts the thread
	public void run() {
		ejecutarMina();
	}

	// Determine move direction on the Street
	private int determineNuevaCalle() {
		if (facingNorth())
			return calleActual + 1;
		if (facingSouth() && calleActual > 0)
			return calleActual - 1;
		return calleActual;
	}

	// Determine move direction on the Avenue
	private int determineNuevaAvenida() {
		if (facingEast())
			return avenidaActual + 1;
		if (facingWest() && avenidaActual > 0)
			return avenidaActual - 1;
		return avenidaActual;
	}

	//
	private boolean logMensaje(String mensaje) {
		System.out.println("tipoRobot: " + tipoRobot + " - id: " + id + " - " + mensaje);
		return (true);
	}

	// Determine if the new position that will try to occupate is free and move to
	// it
	private void mover() {
		int nuevaCalle = determineNuevaCalle();
		int nuevaAvenida = determineNuevaAvenida();
		String posicion = Integer.toString(nuevaCalle) + " - " + Integer.toString(nuevaAvenida);
		// Critical area: Check if the position is occupated. If find it in the
		// objPosiciones, the position it tries to get is occupated, so it gets locked
		// by position semaphore
		ejecutarLog = (debugHabilitado) ? logMensaje("Adquiriendo bloqueo: " + posicion) : false;
		objPosiciones.ocuparPosicion(posicion);
		ejecutarLog = (debugHabilitado) ? logMensaje("Posicion libre") : false;
		try {
			// When position is free, uses sem_move to lock all robots that tries to move.
			sem_move.acquire();
			ejecutarLog = (debugHabilitado) ? logMensaje("Me muevo") : false;
			move();
			posicion = Integer.toString(calleActual) + " - " + Integer.toString(avenidaActual);
			// Release previous position where it was and update robot atrributes
			ejecutarLog = (debugHabilitado) ? logMensaje("Libero posición anterior") : false;
			objPosiciones.retirarPosicion(posicion);
			calleActual = nuevaCalle;
			avenidaActual = nuevaAvenida;
		} catch (InterruptedException exc) {
			System.out.println(exc);
		}
		// Finally, releases the semaphore
		sem_move.release();
		ejecutarLog = (debugHabilitado) ? logMensaje("Semáforo Move liberado") : false;
		// Fin Sección crítica
	}

	// Movement inside the mine. No locking because there's a lock in the vein
	// starting position
	private void moverEnMina() {
		int nuevaCalle = determineNuevaCalle();
		int nuevaAvenida = determineNuevaAvenida();
		ejecutarLog = (debugHabilitado) ? logMensaje("Me muevo en la mina") : false;
		move();
		calleActual = nuevaCalle;
		avenidaActual = nuevaAvenida;
	}

	public static String getActualDateTimeString() {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		return dateTime.format(formatter);

	}

	// Enter to the mine. All robots use it but do different things.
	private void ingresoAlaMina() {
		// This steps are the same for all. They goes to the extracting waiting point
		pasosEntradaComunes();
		// Depending on the robot...
		switch (tipoRobot) {
			case TIPO_MINERO:
				// If it's a Minero, go to the vein position
				pasosEntradaComunesMineroTren();
				ingresoMinero();
				break;
			case TIPO_TREN:
				// If it's a Tren, go to the wait train point and wait until Mineros drops at
				// least 120 beepers
				pasosEntradaComunesMineroTren();
				ingresoTren();
				break;
			case TIPO_EXTRACTOR:
				// If it's a Extractor, waits until the trains drops beepers in the delivery
				// point
				if (!inicioTransporte) {
					try {
						ejecutarLog = (debugHabilitado) ? logMensaje("En punto de espera por beepers") : false;
						sem_extraccion.acquire();
					} catch (InterruptedException exc) {
						System.out.println(exc);
					}
				}
				ingresoExtractor();
				break;
		}
	}

	// Minero and Tren robots use the same steps to enter until they get to Avenue 8
	// Street 11
	private void pasosEntradaComunesMineroTren() {
		irAlMuro();
		turnLeft();
		irAlMuro();
		turnRight();
	}

	// All robots use the same steps to enter until they get to street 1 avenue 1
	private void pasosEntradaComunes() {
		// Trains and Extractors waits until Mineros arrive to the Avenue 1
		if (tipoRobot == TIPO_TREN) {
			try {
				ejecutarLog = (debugHabilitado) ? logMensaje("Esperando por la salida de todos los mineros") : false;
				sem_trenes.acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
		if (tipoRobot == TIPO_EXTRACTOR) {
			try {
				ejecutarLog = (debugHabilitado) ? logMensaje("Esperando por la salida de los mineros y trenes") : false;
				sem_extractor.acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
		ejecutarLog = (debugHabilitado) ? logMensaje("Empiezo ejecucion") : false;
		turnLeft();
		irAlMuro();
		turnLeft();
		// Miners start the Trains when the last Miner arrives to Avenue 1
		if (tipoRobot == TIPO_MINERO) {
			if (calleActual == CALLE_MINERO && avenidaActual == 1)
				minerosSalida++;
			if (minerosSalida == cantidadMineros) {
				try {
					ejecutarLog = (debugHabilitado) ? logMensaje("Solo el último minero puede activar los trenes")
							: false;
					sem_salida.acquire();
				} catch (InterruptedException exc) {
					System.out.println(exc);
				}
				while (sem_trenes.availablePermits() <= (cantidadTrenes - 1))
					sem_trenes.release();
				sem_salida.release();
			}
		}
		// Trains start the Extractors when the last Train arrives to Avenue 1
		if (tipoRobot == TIPO_TREN) {
			if (calleActual == CALLE_TREN && avenidaActual == 1)
				trenesSalida++;
			if (trenesSalida == cantidadTrenes) {
				try {
					ejecutarLog = (debugHabilitado) ? logMensaje("Solo el último tren puede activar los extractores")
							: false;
					sem_salida.acquire();
				} catch (InterruptedException exc) {
					System.out.println(exc);
				}
				while (sem_extractor.availablePermits() <= (cantidadExtractores - 1))
					sem_extractor.release();
				sem_salida.release();
			}
		}
		irAlMuro();
		// At Avenue 1 Street 8, the first Extractor enters and the rest waits until the
		// first exit from the mine.
		if (tipoRobot == TIPO_EXTRACTOR) {
			try {
				ejecutarLog = (debugHabilitado) ? logMensaje("Espero si puedo entrar a la mina") : false;
				sem_extIngreso.acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
		// Enters to the mine and goes to the Extractors wait point
		turnLeft();
		mover();
		turnRight();
		irAlPuntoDeEsperaExtraccion();
		ejecutarLog = (debugHabilitado) ? logMensaje("En punto de espera de extractor") : false;
	}

	// Miners moves until finds Avenue 13 (VETA_AVENIDA).
	// The first one, enters direct to the vein. Second one that arrives, goes to
	// the waiting point.
	private void ingresoMinero() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Buscando el inicio de la veta") : false;
		while (avenidaActual < VETA_AVENIDA)
			mover();
		if (encontroVeta) {
			ejecutarLog = (debugHabilitado) ? logMensaje("Segundo minero, voy al punto de espera") : false;
			turnRight();
			mover();
			turnLeft();
			mover();
			turnLeft();
		} else {
			ejecutarLog = (debugHabilitado) ? logMensaje("Primer minero, encontré la veta") : false;
			encontroVeta = true;
			mover();
		}
	}

	// Trains goes to the waiting point and waits until the Miners delivers at least
	// 120 beepers
	// controlled by the sem_trenInicioProceso
	private void ingresoTren() {
		for (int i = AVENIDA_INICIAL; i < AVENIDA_ESPERA_TREN; i++)
			mover();
		if (!minaVacia) {
			try {
				ejecutarLog = (debugHabilitado)
						? logMensaje("Espero que hayan " + BEEPERS_TREN + " beepers para recogerlos.")
						: false;
				sem_trenInicioProceso.acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
		mover();
	}

	// Extractor moves to the pick point where the Trains drops the 120 beepers per
	// trip
	private void ingresoExtractor() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Voy al punto de recogida") : false;
		mover();
		mover();
	}

	// Avanza hasta que encuentre un muro
	private void irAlMuro() {
		while (frontIsClear())
			mover();
	}

	// All robots goes thru this method.
	private void procesar() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Inicio proceso") : false;
		switch (tipoRobot) {
			case TIPO_MINERO:
				procesarMinero();
				break;
			case TIPO_TREN:
				// Train process maintains while Miners finds beepers in the vein and left them
				// in the drop point
				while (nextToABeeper() || !minaVacia)
					procesarTren();
				turnRight();
				break;
			case TIPO_EXTRACTOR:
				// Extractor process maintains while Trains finds beepers in the drop point and
				// left them in the drop point
				while (nextToABeeper() && (!extraccionCompleta))
					procesarExtractor();
				break;
		}
		ejecutarLog = (debugHabilitado) ? logMensaje("Termino proceso") : false;
	}

	// Extractor moves beepers from the delivery point to the warehouses.
	private void irAlasBodegas() {
		// Go out from the mine to the Avenue 2 Street 7
		ejecutarLog = (debugHabilitado) ? logMensaje("Llevo beepers a las bodegas.") : false;
		salirMina();
		// From there, moves out and go to the warehouses that starts at Avenue 7 Street
		// 7.
		mover();
		turnRight();
		irAlMuro();
		ejecutarLog = (debugHabilitado) ? logMensaje("Dejo entrar al extractor que viene de la casa") : false;
		sem_extIngreso.release();
		turnRight();
		mover();
		turnRight();
		// Finds the current warehouse available
		for (int i = 0; i < bodegaEnUso; i++)
			mover();
		// Drops the beepers until it doesn't have more on the bag.
		// If the warehouse get to it's limit, move to the next.
		ejecutarLog = (debugHabilitado) ? logMensaje("Dejo los beepers que tenga en la bodega en uso.") : false;
		while (anyBeepersInBeeperBag()) {
			putBeeper();
			arr_bodegas[bodegaEnUso]++;
			if (arr_bodegas[bodegaEnUso] == BEEPERS_POR_BODEGA) {
				bodegaEnUso++;
				ejecutarLog = (debugHabilitado) ? logMensaje("Cambio a la bodega " + bodegaEnUso) : false;
				mover();
			}
		}
		// Goes to the waiting entry point on the warehouses...
		irAlMuro();
		turnRight();
		// ... and checks if all beepers are in the warehouses.
		if (!extraccionCompleta) {
			// If not, lock himself until extractor that is in the mine went out
			ejecutarLog = (debugHabilitado) ? logMensaje("Espero que pueda ingresar a seguir extrayendo") : false;
			try {
				sem_extIngreso.acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
			if (!extraccionCompleta) {
				// If there's still beepers in the mine, go back to get them from the Trains
				// drop point.
				ejecutarLog = (debugHabilitado) ? logMensaje("Reingreso a la mina") : false;
				mover();
				turnLeft();
				mover();
				turnLeft();
				irAlPuntoDeEsperaExtraccion();
				mover();
				mover();
			}
		}
	}

	// Steps to go from mine entrance to the Extractor Waiting point. All robots
	// must do this instructions.
	public void irAlPuntoDeEsperaExtraccion() {
		irAlMuro();
		turnRight();
		irAlMuro();
		turnLeft();
		irAlMuro();
		turnLeft();
	}

	// Full Extractor process
	private void procesarExtractor() {
		int beepers = 0;
		// Picks the beepers from the Train Delivery point
		ejecutarLog = (debugHabilitado) ? logMensaje("Tomando beepers del punto de extracción") : false;
		while (nextToABeeper() && beepers < BEEPERS_EXTRACTOR) {
			pickBeeper();
			beepers++;
		}
		// If vein is empty and already put all beepers on the Warehouse, release and
		// allows all miners and trains to go home
		if (minaVacia && beepers == 0) {
			extraccionCompleta = true;
			ejecutarLog = (debugHabilitado) ? logMensaje("Extraccion completa. Todos regresan a casa.") : false;
			while (sem_mineros.availablePermits() <= (cantidadMineros - 1))
				sem_mineros.release();
			while (sem_trenSalida.availablePermits() <= (cantidadTrenes - 1))
				sem_trenSalida.release();
		}
		turnAround();
		if (!extraccionCompleta)
			irAlasBodegas();
	}

	// Train process
	private void procesarTren() {
		int beepers = 0;
		// Pickup the amount of beepers that the train can manage
		ejecutarLog = (debugHabilitado) ? logMensaje("Tomando beepers del punto de delivery de la veta") : false;
		while (nextToABeeper() && beepers < BEEPERS_TREN) {
			pickBeeper();
			beepers++;
			beepersExtraidos--;
		}
		// Go to the delivery point
		turnRight();
		irAlPuntoDeEntrega();
		// When it gets there for the first time, activate the Extractors
		if (!inicioTransporte) {
			ejecutarLog = (debugHabilitado)
					? logMensaje("El primer tren llega con Beepers, se despiertan los Extractores.")
					: false;
			inicioTransporte = true;
			sem_extraccion.release();
		}
		// Delivers all beepers that it has in the bag...
		ejecutarLog = (debugHabilitado) ? logMensaje("Dejando beepers") : false;
		for (int i = beepers; i > 0; i--)
			putBeeper();
		// ... and goes back to the vein delivery point
		turnLeft();
		ejecutarLog = (debugHabilitado) ? logMensaje("Regreso al punto de espera de la veta") : false;
		pasosEntradaComunesMineroTren();
		ingresoTren();
	}

	// With the amount of beepers found it in the vein delivery point, goes to the
	// extractors delivery point at Avenue 3 Street 1
	private void irAlPuntoDeEntrega() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Voy al punto de entrega a los extractores") : false;
		irAlMuro();
		turnRight();
		irAlMuro();
		turnLeft();
		// If there's any beeper in its bag, go to the delivery point at Avenue 3 Street
		// 1. If not, moves or locks until Miner informs that the vein is empty.
		if (anyBeepersInBeeperBag())
			irAlMuro();
		else {
			while (calleActual > (CALLE_ESPERA_EXT + 1))
				mover();
			if (minaVacia && !extraccionCompleta && tipoRobot == TIPO_TREN) {
				ejecutarLog = (debugHabilitado) ? logMensaje("Mina vacia - Esperando que los extractores terminen.")
						: false;
				try {
					sem_trenSalida.acquire();
				} catch (InterruptedException exc) {
					System.out.println(exc);
				}
				ejecutarLog = (debugHabilitado) ? logMensaje("Saliendo a casa") : false;
			}
			mover();
		}
	}

	// Miners process until no more mines in the vein
	private void procesarMinero() {
		int pasosPuntoEntrega = 1;
		int j;

		while (!minaVacia) {
			ejecutarLog = (debugHabilitado) ? logMensaje("Estoy en 10-14?") : false;
			// I'm in the Vein waiting point?
			if (calleActual == VETA_ESPERA_CALLE && avenidaActual == VETA_ESPERA_AVENIDA) {
				// Moves to the vein entry point
				ejecutarLog = (debugHabilitado) ? logMensaje("Si") : false;
				mover();
				turnRight();
			}
			ejecutarLog = (debugHabilitado) ? logMensaje("No estoy en un beeper y tengo la frente libre?") : false;
			// Moves thru the vein until finds beepers or a wall.
			while (!nextToABeeper() && frontIsClear()) {
				moverEnMina();
				ejecutarLog = (debugHabilitado) ? logMensaje("Si") : false;
			}
			// If I'm in beepers...
			ejecutarLog = (debugHabilitado) ? logMensaje("Estoy parado en beepers?") : false;
			if (nextToABeeper())
				// ... picks them. If not...
				recoger(BEEPERS_MINERO);
			else {
				// ... the vein is empty?
				ejecutarLog = (debugHabilitado) ? logMensaje("No. Fondo veta?") : false;
				if (!frontIsClear()) {
					// ... yes! Empty vein...
					ejecutarLog = (debugHabilitado) ? logMensaje("Si") : false;
					fondoVeta = true;
				}
			}
			// Go back to the vein delivery point
			ejecutarLog = (debugHabilitado) ? logMensaje("Salir de la veta") : false;
			turnAround();
			while (avenidaActual > VETA_ESPERA_AVENIDA)
				moverEnMina();
			ejecutarLog = (debugHabilitado) ? logMensaje("Mina vacia y no hay extraccion completa?") : false;
			// Miners finished but all beepers aren't in the warehouses
			if (minaVacia && !extraccionCompleta) {
				// So, miners go locked
				ejecutarLog = (debugHabilitado) ? logMensaje("Si. Me bloqueo hasta que terminen de extraer") : false;
				try {
					sem_mineros.acquire();
				} catch (InterruptedException exc) {
					System.out.println(exc);
				}
			}
			// Trains aren't working because they didn´t have beepers to pick. So, miners
			// allows them to move.
			ejecutarLog = (debugHabilitado) ? logMensaje("Me muevo") : false;
			mover();
			ejecutarLog = (debugHabilitado) ? logMensaje("No ha iniciado extracción?") : false;
			if (!inicioExtraccion && beepersExtraidos >= BEEPERS_TREN) {
				ejecutarLog = (debugHabilitado) ? logMensaje("Notifica que si inició") : false;
				inicioExtraccion = true;
				ejecutarLog = (debugHabilitado)
						? logMensaje("" + sem_trenInicioProceso.availablePermits() + " <= " + (cantidadTrenes - 1))
						: false;
				while (sem_trenInicioProceso.availablePermits() <= (cantidadTrenes - 1))
					sem_trenInicioProceso.release();
			}
			// A robot went to the end of the vein, so there's no more beepers.
			ejecutarLog = (debugHabilitado) ? logMensaje("Llegó al fondo de la veta??") : false;
			if (fondoVeta) {
				ejecutarLog = (debugHabilitado) ? logMensaje("Si --> mina vacia") : false;
				minaVacia = true;
			}
			ejecutarLog = (debugHabilitado) ? logMensaje("Descargar") : false;
			// Unload beepers in the vein delivery point
			descargar();
			ejecutarLog = (debugHabilitado)
					? logMensaje("No está la mina vacia o si está y no ha terminado de extraer?")
					: false;
			// If there's still beepers in the vein...
			if (!minaVacia || (minaVacia && !extraccionCompleta)) {
				// ... I'll go to the vein waiting point
				ejecutarLog = (debugHabilitado) ? logMensaje("Voy a la espera") : false;
				turnLeft();
				mover();
				turnLeft();
				mover();
				turnLeft();
			}
		}
		// No beepers on the Vein. So I wait until the beepers went fully to the
		// warehouses
		ejecutarLog = (debugHabilitado) ? logMensaje("Mina vacia. Estoy en calle 10 - avenida 14?") : false;
		if (calleActual == VETA_ESPERA_CALLE && avenidaActual == VETA_ESPERA_AVENIDA) {
			ejecutarLog = (debugHabilitado) ? logMensaje("Si, me bloqueo hasta que pueda salir.") : false;
			mover();
			turnLeft();
			try {
				sem_mineros.acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
			mover();
		}
		// Finally, turn left to go out.
		ejecutarLog = (debugHabilitado) ? logMensaje("Termina proceso. Giro izquierda") : false;
		turnLeft();
	}

	// Picking a number of beepers from the vein
	private void recoger(int numero) {
		int i = 0;
		ejecutarLog = (debugHabilitado) ? logMensaje("Recojo beepers en la veta") : false;
		while (i < numero) {
			if (nextToABeeper()) {
				pickBeeper();
				i++;
			} else {
				if (!frontIsClear())
					break;
				moverEnMina();
			}
		}
	}

	// Miners puts beepers in the vein dropping point. If they have at least 120
	// dropped, release the trains from the waiting point
	private void descargar() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Descargando en el punto de espera de la veta.") : false;
		while (anyBeepersInBeeperBag()) {
			putBeeper();
			beepersExtraidos++;
		}
		ejecutarLog = (debugHabilitado)
				? logMensaje("Termine descarga. Si hay más de " + BEEPERS_TREN
						+ " beepers en el punto de espera, suelto un tren.")
				: false;
		if (beepersExtraidos >= BEEPERS_TREN)
			sem_trenInicioProceso.release();
	}

	// All robots use this method.
	private void salirDeLaMina() {
		switch (tipoRobot) {
			// Miners and Trains do this method
			case 1:
			case 2:
				pasosSalidaMineroTren();
				break;
			// Extractors releases locks from Miners and Trains to leave the Mine
			case 3:
				extraccionCompleta = true;
				ejecutarLog = (debugHabilitado) ? logMensaje("Libero robots y voy a casa.") : false;
				while (sem_mineros.availablePermits() <= (cantidadMineros - 1))
					sem_mineros.release();
				while (sem_trenSalida.availablePermits() <= (cantidadTrenes - 1))
					sem_trenSalida.release();
				if (calleActual == CALLE_ESPERA_EXT && avenidaActual == AVENIDA_ESPERA_EXT)
					turnAround();
				break;
		}
		// All robots do this to leave the mine from the extracting point at Avenue 3
		// Street 1
		pasosSalidaComunes();
	}

	// Route from the extracting point at Avenue 3 Street 1 to the main entrance of
	// the Mine
	private void salirMina() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Salgo a la puerta de la mina.") : false;
		irAlMuro();
		turnRight();
		irAlMuro();
		turnRight();
		irAlMuro();
		turnLeft();
		// Extractor in mine release the lock for the other
		if (tipoRobot == TIPO_EXTRACTOR && extraccionCompleta) {
			ejecutarLog = (debugHabilitado) ? logMensaje("Libero extractor que está en bodega.") : false;
			sem_extIngreso.release();
		}
	}

	// All robots use this when finish the process
	private void pasosSalidaComunes() {
		// All robots in the mine do this, except the robot in the Warehouse
		if (tipoRobot <= TIPO_TREN || (tipoRobot == TIPO_EXTRACTOR && calleActual == CALLE_ESPERA_EXT
				&& avenidaActual == AVENIDA_ESPERA_EXT))
			salirMina();
		// All robots go to the Initial Street + 1
		ejecutarLog = (debugHabilitado) ? logMensaje("Buscando mi casa.") : false;
		for (int i = calleActual; i <= calleInicial; i++)
			mover();
		turnRight();
		// Then they go to the Avenue they started...
		for (int i = avenidaActual; i < avenidaInicial; i++)
			mover();
		// And park.
		turnRight();
		mover();
		turnAround();
		ejecutarLog = (debugHabilitado) ? logMensaje("Hora de dormir. Dulces sueños.") : false;
		turnOff();
	}

	// Moves to the Train dropoff point when exit.
	private void pasosSalidaMineroTren() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Voy al punto de entrega al extractor.") : false;
		irAlPuntoDeEntrega();
		turnRight();
	}

	// Move the Miners to the train route for exit
	private void sacarMinero() {
		if (!facingWest()) {
			ejecutarLog = (debugHabilitado) ? logMensaje("Salgo del punto de espera.") : false;
			mover();
			turnLeft();
		}
		mover();
		turnLeft();
	}

	// All robots does this 3 steps
	private void ejecutarMina() {
		ejecutarLog = (debugHabilitado) ? logMensaje("Empiezo proceso.") : false;
		ingresoAlaMina();
		procesar();
		salirDeLaMina();
	}

	// Create the robots in the World due to the type
	public static void crearRobots(int tipoRobot) {
		Color colorRobot = Color.WHITE;
		int calle = 0;
		int cantidad = 0;
		Minero robot;

		// Parameters for the robots due to the type
		switch (tipoRobot) {
			case TIPO_MINERO:
				calle = CALLE_MINERO;
				colorRobot = Color.BLACK;
				cantidad = cantidadMineros;
				break;
			case TIPO_TREN:
				calle = CALLE_TREN;
				colorRobot = Color.BLUE;
				cantidad = cantidadTrenes;
				break;
			case TIPO_EXTRACTOR:
				calle = CALLE_EXTRACTOR;
				colorRobot = Color.RED;
				cantidad = cantidadExtractores;
				break;
		}
		// Creates the number of robots defined and adds to the ArrayList and to the
		// Threads
		for (int i = AVENIDA_INICIAL; i < (AVENIDA_INICIAL + cantidad); i++) {
			robot = new Minero(calle, i, North, 0, colorRobot, tipoRobot, i - AVENIDA_INICIAL);
			Minero.objRobots.add(robot);
			Minero.objThreads.add(new Thread(robot));
			String stringCrearRobot = robot.toString();
			JSONParser parser = new JSONParser();

			try {
				Minero.db_client.insert("RobotDB", "Robot",
						(JSONObject) parser.parse(convertirFormatoRobot(stringCrearRobot, tipoRobot)));
			} catch (ParseException e) {

				e.printStackTrace();
			}
		}
	}

	// Verifies if a String contains only numbers
	private static boolean isNumeric(final CharSequence cs) {
		if (cs.length() == 0) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// Verify arguments and modify parameters as find it
	private static void validarArgumentos(String[] args) {
		int valor;

		// Checking arguments
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-d")) {
					Minero.debugHabilitado = true;
				} else {
					if (!args[i].equals("-m") && !args[i].equals("-t") && !args[i].equals("-e")) {
						System.out.println("No se reconoce el comando " + args[i] + ". El programa termina.");
						System.exit(1);
					}
					if ((i + 1) == args.length) {
						System.out.println("Faltan paramentros en el comando. El programa termina.");
						System.exit(2);
					}
					if (!isNumeric(args[i + 1])) {
						System.out
								.println("Se esperaba un numero para el comando " + args[0] + ". El programa termina.");
						System.exit(2);
					}
					valor = Integer.parseInt(args[i + 1]);
					if (args[i].equals("-m")) {
						if (valor > cantidadMineros)
							Minero.cantidadMineros = valor;
					}
					if (args[i].equals("-t")) {
						if (valor > cantidadTrenes)
							Minero.cantidadTrenes = valor;
					}
					if (args[i].equals("-e")) {
						if (valor > cantidadExtractores)
							Minero.cantidadExtractores = valor;
					}
					i++;
				}
			}
		}
		if (debugHabilitado)
			System.out.println("Debug Habilitado.");
	}

	// Setup global variables
	private static void asignarVariablesEstaticas() {
		// Setup warehouses
		Minero.arr_bodegas = new int[NUMERO_BODEGAS];
		Minero.bodegaEnUso = 0;
		// Setup objects for program control
		Minero.objRobots = new ArrayList<Object>();
		Minero.objPosiciones = new Posiciones();
		Minero.objThreads = new ArrayList<Thread>();
		// Setup semaphores
		Minero.sem_extIngreso = new Semaphore(1);
		Minero.sem_extraccion = new Semaphore(0);
		Minero.sem_extractor = new Semaphore(0);
		Minero.sem_mineros = new Semaphore(0);
		Minero.sem_move = new Semaphore(1);
		Minero.sem_salida = new Semaphore(1);
		Minero.sem_trenes = new Semaphore(0);
		Minero.sem_trenInicioProceso = new Semaphore(0);
		Minero.sem_trenSalida = new Semaphore(0);
	}

	// Setup Karel World
	private static void setupWorld(String mundo) {
		World.readWorld(mundo);
		World.setTrace(false);
		World.setDelay(10);
		World.setVisible(true);
		World.showSpeedControl(true, true); // Needed to make them start
	}

	// Main method
	public static void main(String[] args) throws InterruptedException {
		asignarVariablesEstaticas();
		validarArgumentos(args);
		setupWorld(
				"/home/danicracker/university/septimo_semestre/Sistemas Operativos/Projecto3/Karel/Trabajos/Mina.kwld");
		Minero.db_client = new KarelDbClient();
		Minero.db_client.startConnection("localhost", 2025);

		// Creates robots in the world
		crearRobots(TIPO_MINERO);
		crearRobots(TIPO_TREN);
		crearRobots(TIPO_EXTRACTOR);

		// Initialize the threads
		for (int i = 0; i < objThreads.size(); i++)
			objThreads.get(i).start();
	}
}

class Posiciones {
	// Hashtable to manage the position locks
	private Hashtable<String, Posicion> posiciones;

	public Posiciones() {
		posiciones = new Hashtable<>();
	}

	// Check if the position requested is already occupated by a robot. If so, locks
	// the thread.
	// If don´t, creates the position and it's lock.
	public void ocuparPosicion(String posicion) {
		if (posiciones.containsKey(posicion)) {
			try {
				posiciones.get(posicion).getSemaforo().acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		} else {
			Semaphore sem = new Semaphore(0);
			Posicion objPosicion = new Posicion(sem);
			posiciones.put(posicion, objPosicion);
		}
	}

	// When the robot leaves the position, retires the position and the locks
	public void retirarPosicion(String posicion) {
		Semaphore sem = posiciones.get(posicion).getSemaforo();
		if (sem.availablePermits() == -1)
			posiciones.remove(posicion);
		sem.release();
	}
}

class Posicion {
	private Semaphore sem;

	public Posicion(Semaphore sem) {
		this.sem = sem;
	}

	// Return the Semaphore of this position
	public Semaphore getSemaforo() {
		return sem;
	}
}
