package co.edu.usc.voltacali;

import java.util.Vector;

public class CargadorVE {

// Parte D a: Enums anidados en el orden exacto exigido
public enum TipoConector {
    TIPO_1, TIPO_2, CCS2, CHADEMO, GBT
}

public enum TipoCargador {
    MURAL, PEDESTAL, RAPIDO_DC, ULTRARRAPIDO, PORTATIL, BIDIRECCIONAL_V2G
}

public enum Ubicacion {
    CENTRO_COMERCIAL, UNIVERSIDAD, ESTACION_SERVICIO, PARQUEADERO_PUBLICO, 
    RESIDENCIAL, HOTEL, TERMINAL, FLOTA_CORPORATIVA
}

// Parte D e: Miembros estáticos y constantes
public static final double LIMITE_RED = 50.0;
public static final double INCREMENTO_DEFECTO = 5.0;
private static int totalCargadores = 0;
private static int contadorRegistros = 0;

// Parte A: Atributos privados
private String fabricante;
private int anioInstalacion;
private int voltajeNominal;
private TipoConector tipoConector;
private TipoCargador tipoCargador;
private int numeroConectores;
private int puestosParqueo;
private double potenciaMaxima;
private Ubicacion ubicacion;
private double potenciaActual;

// Parte D c: Bitácora mediante Vector
private Vector<RegistroSesion> bitacora;

// Parte D b: Clase interna no estática
public class RegistroSesion {
    private int idRegistro;
    private String marca;
    private int anio;
    private double potenciaAlRegistro;
    private String descripcionEvento;
    private boolean esValido;

    public RegistroSesion(String evento, boolean valido) {
        contadorRegistros++;
        this.idRegistro = contadorRegistros;
        // Captura de atributos del objeto externo directamente
        this.marca = fabricante;
        this.anio = anioInstalacion;
        this.potenciaAlRegistro = potenciaActual;
        this.descripcionEvento = evento;
        this.esValido = valido;
    }

    public String describir() {
        String estado = esValido ? "ÉXITO" : "RECHAZADO";
        return String.format("#%d [%s] %s (%d) - Potencia: %.1f kW - Evento: %s",
                idRegistro, estado, marca, anio, potenciaAlRegistro, descripcionEvento);
    }

    public double getPotenciaAlRegistro() { return potenciaAlRegistro; }
    public boolean isValido() { return esValido; }
}

// Parte C: Constructores sobrecargados
public CargadorVE(String fabricante, int anioInstalacion, int voltajeNominal, 
                  TipoConector tipoConector, TipoCargador tipoCargador, 
                  int numeroConectores, int puestosParqueo, double potenciaMaxima, 
                  Ubicacion ubicacion) {
    this.fabricante = fabricante;
    this.anioInstalacion = anioInstalacion;
    this.voltajeNominal = voltajeNominal;
    this.tipoConector = tipoConector;
    this.tipoCargador = tipoCargador;
    this.numeroConectores = numeroConectores;
    this.puestosParqueo = puestosParqueo;
    this.potenciaMaxima = potenciaMaxima;
    this.ubicacion = ubicacion;
    this.potenciaActual = 0.0;
    this.bitacora = new Vector<>();
    totalCargadores++;
}

public CargadorVE(String fabricante, int anioInstalacion, double potenciaMaxima) {
    this(fabricante, anioInstalacion, 220, TipoConector.TIPO_2, 
         TipoCargador.PEDESTAL, 1, 1, potenciaMaxima, Ubicacion.PARQUEADERO_PUBLICO);
}

public CargadorVE(CargadorVE otro) {
    if (otro != null) {
        this.fabricante = otro.fabricante;
        this.anioInstalacion = otro.anioInstalacion;
        this.voltajeNominal = otro.voltajeNominal;
        this.tipoConector = otro.tipoConector;
        this.tipoCargador = otro.tipoCargador;
        this.numeroConectores = otro.numeroConectores;
        this.puestosParqueo = otro.puestosParqueo;
        this.potenciaMaxima = otro.potenciaMaxima;
        this.ubicacion = otro.ubicacion;
        this.potenciaActual = 0.0;
        this.bitacora = new Vector<>();
        totalCargadores++;
    }
}

// Parte A: Getters y Setters
public String getFabricante() { return fabricante; }
public void setFabricante(String fabricante) { this.fabricante = fabricante; }

public int getAnioInstalacion() { return anioInstalacion; }
public void setAnioInstalacion(int anioInstalacion) { this.anioInstalacion = anioInstalacion; }

public int getVoltajeNominal() { return voltajeNominal; }
public void setVoltajeNominal(int voltajeNominal) { this.voltajeNominal = voltajeNominal; }

public TipoConector getTipoConector() { return tipoConector; }
public void setTipoConector(TipoConector tipoConector) { this.tipoConector = tipoConector; }

public TipoCargador getTipoCargador() { return tipoCargador; }
public void setTipoCargador(TipoCargador tipoCargador) { this.tipoCargador = tipoCargador; }

public int getNumeroConectores() { return numeroConectores; }
public void setNumeroConectores(int numeroConectores) { this.numeroConectores = numeroConectores; }

public int getPuestosParqueo() { return puestosParqueo; }
public void setPuestosParqueo(int puestosParqueo) { this.puestosParqueo = puestosParqueo; }

public double getPotenciaMaxima() { return potenciaMaxima; }
public void setPotenciaMaxima(double potenciaMaxima) { this.potenciaMaxima = potenciaMaxima; }

public Ubicacion getUbicacion() { return ubicacion; }
public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }

public double getPotenciaActual() { return potenciaActual; }

public void setPotenciaActual(double potenciaActual) {
    if (potenciaActual < 0 || potenciaActual > this.potenciaMaxima) {
        System.out.println("-> [ERROR] Potencia fuera de rango: " + potenciaActual + " kW");
        bitacora.add(new RegistroSesion("Rechazado setPotenciaActual: " + potenciaActual + " kW", false));
    } else {
        this.potenciaActual = potenciaActual;
        bitacora.add(new RegistroSesion("Aceptado setPotenciaActual: " + potenciaActual + " kW", true));
    }
}

public Vector<RegistroSesion> getBitacora() { return bitacora; }

// Parte B & C: Métodos de modificación de potencia y sobrecargas
public void aumentarPotencia() {
    aumentarPotencia(INCREMENTO_DEFECTO);
}

public void aumentarPotencia(double incremento) {
    double nueva = this.potenciaActual + incremento;
    if (nueva > this.potenciaMaxima || nueva < 0) {
        System.out.println("-> [ERROR] Incremento excede límite: " + nueva + " kW");
        bitacora.add(new RegistroSesion("Rechazado aumentarPotencia(" + incremento + ")", false));
    } else {
        this.potenciaActual = nueva;
        bitacora.add(new RegistroSesion("Aceptado aumentarPotencia(+" + incremento + " kW)", true));
    }
}

public boolean aumentarPotencia(double incremento, int veces) {
    boolean interrumpe = false;
    for (int i = 0; i < veces; i++) {
        double nueva = this.potenciaActual + incremento;
        if (nueva > this.potenciaMaxima || nueva < 0) {
            System.out.println("-> [ERROR] Paso " + (i + 1) + " rechazado.");
            bitacora.add(new RegistroSesion("Paso " + (i + 1) + " rechazado en incremento por pasos", false));
            interrumpe = true;
            break;
        } else {
            this.potenciaActual = nueva;
            bitacora.add(new RegistroSesion("Paso " + (i + 1) + " aplicado (+" + incremento + " kW)", true));
        }
    }
    return interrumpe;
}

public void reducirPotencia(double decremento) {
    double nueva = this.potenciaActual - decremento;
    if (nueva < 0 || nueva > this.potenciaMaxima) {
        System.out.println("-> [ERROR] Reducción no válida: " + nueva + " kW");
        bitacora.add(new RegistroSesion("Rechazado reducirPotencia(-" + decremento + " kW)", false));
    } else {
        this.potenciaActual = nueva;
        bitacora.add(new RegistroSesion("Aceptado reducirPotencia(-" + decremento + " kW)", true));
    }
}

public void cortarCarga() {
    this.potenciaActual = 0.0;
    bitacora.add(new RegistroSesion("Aceptado cortarCarga (0.0 kW)", true));
}

// Parte B & C: Tiempos estimados sobrecargados
public double tiempoEstimadoCarga(double energiaKWh) {
    if (this.potenciaActual <= 0) {
        System.out.println("-> [ERROR] Potencia en cero. Carga no realizable.");
        return -1.0;
    }
    return energiaKWh / this.potenciaActual;
}

public double tiempoEstimadoCarga(double energiaKWh, double potenciaProgramada) {
    if (potenciaProgramada <= 0) {
        System.out.println("-> [ERROR] Potencia programada inválida.");
        return -1.0;
    }
    return energiaKWh / potenciaProgramada;
}

public double tiempoEstimadoCarga(double energiaKWh, int pausas, double minutosPorPausa) {
    double tiempoBase = tiempoEstimadoCarga(energiaKWh);
    if (tiempoBase == -1.0) return -1.0;
    return tiempoBase + ((pausas * minutosPorPausa) / 60.0);
}

// Parte B & C: Métodos de visualización sobrecargados
public void mostrar() {
    mostrar(false);
}

public void mostrar(boolean detallado) {
    System.out.println("==================================================");
    System.out.println(" Cargador: " + fabricante + " (" + anioInstalacion + ")");
    System.out.println(" Voltaje: " + voltajeNominal + "V | Potencia Max: " + potenciaMaxima + " kW | Actual: " + potenciaActual + " kW");
    System.out.println(" Conector: " + tipoConector + " | Tipo: " + tipoCargador + " | Ubicación: " + ubicacion);
    if (detallado) {
        System.out.println(" --- Bitácora de Eventos (" + bitacora.size() + ") ---");
        for (RegistroSesion rs : bitacora) {
            System.out.println("  " + rs.describir());
        }
    }
    System.out.println("==================================================");
}

// Parte D e & d: Estadísticas y manejo seguro contra valores null
public static int getTotalCargadores() { return totalCargadores; }
public static int getContadorRegistros() { return contadorRegistros; }

public static int[] contarPorTipo(CargadorVE[] flota) {
    int[] conteo = new int[TipoCargador.values().length];
    if (flota == null) return conteo;
    for (CargadorVE c : flota) {
        if (c != null && c.getTipoCargador() != null) {
            conteo[c.getTipoCargador().ordinal()]++;
        }
    }
    return conteo;
}

public static double promedioPotencia(CargadorVE[] flota) {
    if (flota == null) return 0.0;
    double suma = 0.0;
    int cont = 0;
    for (CargadorVE c : flota) {
        if (c != null) {
            suma += c.getPotenciaActual();
            cont++;
        }
    }
    return cont == 0 ? 0.0 : suma / cont;
}

public static CargadorVE mayorPotencia(CargadorVE[] flota) {
    if (flota == null) return null;
    CargadorVE mayor = null;
    for (CargadorVE c : flota) {
        if (c != null) {
            if (mayor == null || c.getPotenciaActual() > mayor.getPotenciaActual()) {
                mayor = c;
            }
        }
    }
    return mayor;
}

public static int excesosDePotenciaContratada(CargadorVE[] flota) {
    if (flota == null) return 0;
    int conteo = 0;
    for (CargadorVE c : flota) {
        if (c != null) {
            for (RegistroSesion rs : c.getBitacora()) {
                if (rs.isValido() && rs.getPotenciaAlRegistro() > LIMITE_RED) {
                    conteo++;
                }
            }
        }
    }
    return conteo;
}

// Parte C: Métodos estáticos de filtrado
public static CargadorVE[] filtrar(CargadorVE[] flota, TipoConector conector) {
    if (flota == null) return new CargadorVE[0];
    int num = 0;
    for (CargadorVE c : flota) {
        if (c != null && c.getTipoConector() == conector) num++;
    }
    CargadorVE[] res = new CargadorVE[num];
    int idx = 0;
    for (CargadorVE c : flota) {
        if (c != null && c.getTipoConector() == conector) res[idx++] = c;
    }
    return res;
}

public static CargadorVE[] filtrar(CargadorVE[] flota, TipoCargador tipo) {
    if (flota == null) return new CargadorVE[0];
    int num = 0;
    for (CargadorVE c : flota) {
        if (c != null && c.getTipoCargador() == tipo) num++;
    }
    CargadorVE[] res = new CargadorVE[num];
    int idx = 0;
    for (CargadorVE c : flota) {
        if (c != null && c.getTipoCargador() == tipo) res[idx++] = c;
    }
    return res;
}

public static CargadorVE[] filtrar(CargadorVE[] flota, Ubicacion ubicacion) {
    if (flota == null) return new CargadorVE[0];
    int num = 0;
    for (CargadorVE c : flota) {
        if (c != null && c.getUbicacion() == ubicacion) num++;
    }
    CargadorVE[] res = new CargadorVE[num];
    int idx = 0;
    for (CargadorVE c : flota) {
        if (c != null && c.getUbicacion() == ubicacion) res[idx++] = c;
    }
    return res;
}

// Método para la Ruta Individual (Ruta 3)
public static void sesionesSobreLimiteRed(CargadorVE[] flota) {
    if (flota == null) {
        System.out.println("Flota nula.");
        return;
    }
    boolean hallado = false;
    for (CargadorVE c : flota) {
        if (c != null) {
            for (RegistroSesion rs : c.getBitacora()) {
                if (rs.isValido() && rs.getPotenciaAlRegistro() > LIMITE_RED) {
                    System.out.println("  " + rs.describir());
                    hallado = true;
                }
            }
        }
    }
    if (!hallado) {
        System.out.println("No hay sesiones válidas registradas por encima del límite de red (50 kW).");
    }
}

}
