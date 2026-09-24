package co.edu.usc.voltacali;

import java.util.Vector;

public class CargadorVE {

    // Parte D.a: Enums anidados (orden estricto)
    public enum TipoConector { TIPO_1, TIPO_2, CCS2, CHADEMO, GBT }
    public enum TipoCargador { MURAL, PEDESTAL, RAPIDO_DC, ULTRARRAPIDO, PORTATIL, BIDIRECCIONAL_V2G }
    public enum Ubicacion { CENTRO_COMERCIAL, UNIVERSIDAD, ESTACION_SERVICIO, PARQUEADERO_PUBLICO, RESIDENCIAL, HOTEL, TERMINAL, FLOTA_CORPORATIVA }

    // Parte D.e: Miembros estáticos
    public static int totalCargadores = 0;
    public static int contadorRegistros = 1;
    public static final double LIMITE_RED = 50.0;
    public static final double INCREMENTO_DEFECTO = 5.0;

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

    // Parte D.c: Bitácora
    private Vector<RegistroSesion> bitacora;

    // Parte D.b: Clase interna no estática
    public class RegistroSesion {
        private int numeroRegistro;
        private String evento;
        private boolean valido;
        private String fabricanteReg;
        private int anioInstalacionReg;
        private double potenciaActualReg;

        public RegistroSesion(String evento, boolean valido) {
            this.numeroRegistro = contadorRegistros++;
            this.evento = evento;
            this.valido = valido;
            // Captura de datos del objeto externo
            this.fabricanteReg = CargadorVE.this.fabricante;
            this.anioInstalacionReg = CargadorVE.this.anioInstalacion;
            this.potenciaActualReg = CargadorVE.this.potenciaActual;
        }

        public String describir() {
            return "Reg #" + numeroRegistro + " [" + fabricanteReg + "-" + anioInstalacionReg + "] " +
                   "Potencia: " + potenciaActualReg + "kW - Evento: " + evento + " - Válido: " + valido;
        }

        public boolean isValido() { return valido; }
        public double getPotenciaActualReg() { return potenciaActualReg; }
    }

    // Parte C: Constructores
    // 1. Completo
    public CargadorVE(String fabricante, int anioInstalacion, int voltajeNominal,
                      TipoConector tipoConector, TipoCargador tipoCargador,
                      int numeroConectores, int puestosParqueo,
                      double potenciaMaxima, Ubicacion ubicacion) {
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

    // 2. Reducido
    public CargadorVE(String fabricante, int anioInstalacion, double potenciaMaxima) {
        this(fabricante, anioInstalacion, 220, TipoConector.TIPO_2, TipoCargador.PEDESTAL,
             1, 1, potenciaMaxima, Ubicacion.PARQUEADERO_PUBLICO);
    }

    // 3. Copia
    public CargadorVE(CargadorVE otro) {
        this(otro.fabricante, otro.anioInstalacion, otro.voltajeNominal,
             otro.tipoConector, otro.tipoCargador, otro.numeroConectores,
             otro.puestosParqueo, otro.potenciaMaxima, otro.ubicacion);
    }

    // Parte A y B: Métodos de Comportamiento Base
    public void setPotenciaActual(double potencia) {
        if (potencia < 0 || potencia > potenciaMaxima) {
            System.out.println("  -> Rechazado: " + potencia + " kW es inválido (Fuera de rango 0 - " + potenciaMaxima + ")");
            bitacora.add(new RegistroSesion("setPotenciaActual(" + potencia + ")", false));
        } else {
            this.potenciaActual = potencia;
            bitacora.add(new RegistroSesion("setPotenciaActual", true));
        }
    }

    public void aumentarPotencia(double incremento) {
        double nuevaPotencia = this.potenciaActual + incremento;
        if (nuevaPotencia > potenciaMaxima) {
            System.out.println("  -> Rechazado: Supera potencia máxima (" + potenciaMaxima + " kW)");
            bitacora.add(new RegistroSesion("aumentarPotencia(" + incremento + ")", false));
        } else {
            this.potenciaActual = nuevaPotencia;
            bitacora.add(new RegistroSesion("aumentarPotencia", true));
        }
    }

    public void reducirPotencia(double reduccion) {
        double nuevaPotencia = this.potenciaActual - reduccion;
        if (nuevaPotencia < 0) {
            System.out.println("  -> Rechazado: La potencia no puede ser negativa.");
            bitacora.add(new RegistroSesion("reducirPotencia(" + reduccion + ")", false));
        } else {
            this.potenciaActual = nuevaPotencia;
            bitacora.add(new RegistroSesion("reducirPotencia", true));
        }
    }

    public void cortarCarga() {
        this.potenciaActual = 0.0;
        bitacora.add(new RegistroSesion("cortarCarga", true));
    }

    // Parte C: Sobrecarga
    public void aumentarPotencia() {
        aumentarPotencia(INCREMENTO_DEFECTO);
    }

    public void aumentarPotencia(double incremento, int veces) {
        for (int i = 0; i < veces; i++) {
            double nuevaPotencia = this.potenciaActual + incremento;
            if (nuevaPotencia > potenciaMaxima) {
                System.out.println("  -> Rechazado en paso " + (i + 1) + ": Supera límite.");
                bitacora.add(new RegistroSesion("aumentarPotencia escalonado", false));
                break;
            } else {
                this.potenciaActual = nuevaPotencia;
                bitacora.add(new RegistroSesion("aumentarPotencia escalonado", true));
            }
        }
    }

    public double tiempoEstimadoCarga(double energiaKWh) {
        if (this.potenciaActual == 0) {
            System.out.println("  -> Error: Potencia es 0.");
            return -1.0;
        }
        return energiaKWh / this.potenciaActual;
    }

    public double tiempoEstimadoCarga(double energiaKWh, double potenciaProgramada) {
        if (potenciaProgramada == 0) {
            System.out.println("  -> Error: Potencia programada es 0.");
            return -1.0;
        }
        return energiaKWh / potenciaProgramada;
    }

    public double tiempoEstimadoCarga(double energiaKWh, int pausas, double minutosPorPausa) {
        double tiempoBase = tiempoEstimadoCarga(energiaKWh);
        if (tiempoBase == -1.0) return -1.0;
        double tiempoPausasHoras = (pausas * minutosPorPausa) / 60.0;
        return tiempoBase + tiempoPausasHoras;
    }

    public void mostrar() {
        mostrar(false);
    }

    public void mostrar(boolean detallado) {
        System.out.println("Cargador: " + fabricante + " | Año: " + anioInstalacion + " | Voltaje: " + voltajeNominal + "V");
        System.out.println("Conector: " + tipoConector + " | Tipo: " + tipoCargador + " | Ubicación: " + ubicacion);
        System.out.println("Conectores: " + numeroConectores + " | Puestos: " + puestosParqueo);
        System.out.println("Potencia Max: " + potenciaMaxima + "kW | Potencia Actual: " + potenciaActual + "kW");
        if (detallado) {
            System.out.println("--- Bitácora (" + bitacora.size() + " registros) ---");
            for (RegistroSesion r : bitacora) {
                System.out.println("  " + r.describir());
            }
        }
    }

    // Parte C y D: Métodos estáticos (Arreglos y Filtros)
    public static CargadorVE[] filtrar(CargadorVE[] flota, TipoConector conector) {
        if (flota == null) return new CargadorVE[0];
        int count = 0;
        for (CargadorVE c : flota) if (c != null && c.getTipoConector() == conector) count++;
        CargadorVE[] filtrado = new CargadorVE[count];
        int idx = 0;
        for (CargadorVE c : flota) if (c != null && c.getTipoConector() == conector) filtrado[idx++] = c;
        return filtrado;
    }

    public static CargadorVE[] filtrar(CargadorVE[] flota, TipoCargador tipo) {
        if (flota == null) return new CargadorVE[0];
        int count = 0;
        for (CargadorVE c : flota) if (c != null && c.getTipoCargador() == tipo) count++;
        CargadorVE[] filtrado = new CargadorVE[count];
        int idx = 0;
        for (CargadorVE c : flota) if (c != null && c.getTipoCargador() == tipo) filtrado[idx++] = c;
        return filtrado;
    }

    public static CargadorVE[] filtrar(CargadorVE[] flota, Ubicacion ubi) {
        if (flota == null) return new CargadorVE[0];
        int count = 0;
        for (CargadorVE c : flota) if (c != null && c.getUbicacion() == ubi) count++;
        CargadorVE[] filtrado = new CargadorVE[count];
        int idx = 0;
        for (CargadorVE c : flota) if (c != null && c.getUbicacion() == ubi) filtrado[idx++] = c;
        return filtrado;
    }

    public static int[] contarPorTipo(CargadorVE[] flota) {
        int[] conteo = new int[TipoCargador.values().length];
        if (flota == null) return conteo;
        for (CargadorVE c : flota) {
            if (c != null) {
                conteo[c.getTipoCargador().ordinal()]++;
            }
        }
        return conteo;
    }

    public static double promedioPotencia(CargadorVE[] flota) {
        if (flota == null) return 0.0;
        double suma = 0;
        int count = 0;
        for (CargadorVE c : flota) {
            if (c != null) {
                suma += c.getPotenciaMaxima();
                count++;
            }
        }
        return count == 0 ? 0.0 : suma / count;
    }

    public static CargadorVE mayorPotencia(CargadorVE[] flota) {
        if (flota == null) return null;
        CargadorVE mayor = null;
        for (CargadorVE c : flota) {
            if (c != null) {
                if (mayor == null || c.getPotenciaMaxima() > mayor.getPotenciaMaxima()) {
                    mayor = c;
                }
            }
        }
        return mayor;
    }

    public static int excesosDePotenciaContratada(CargadorVE[] flota) {
        if (flota == null) return 0;
        int excesos = 0;
        for (CargadorVE c : flota) {
            if (c != null) {
                for (RegistroSesion r : c.getBitacora()) {
                    if (r.isValido() && r.getPotenciaActualReg() > LIMITE_RED) {
                        excesos++;
                    }
                }
            }
        }
        return excesos;
    }

    // Getters y Setters obligatorios
    public static int getTotalCargadores() { return totalCargadores; }
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
    public Vector<RegistroSesion> getBitacora() { return bitacora; }
}