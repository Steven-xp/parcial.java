package co.edu.usc.voltacali;

public class App {

    public static void main(String[] args) {
        
        // ==========================================
        // CONFIGURACIÓN ESTUDIANTE:
        // ==========================================
        // REEMPLAZA ESTE VALOR CON LOS DOS ÚLTIMOS DÍGITOS DE TU CÉDULA
        // (Ejemplo: si termina en 47, N = 47; si termina en 07, N = 7)
        int N = 47; 
        // ==========================================

        System.out.println("--- INICIO CASO DE PRUEBA OBLIGATORIO ---");

        // PASO 1: Crear la flota
        CargadorVE c1 = new CargadorVE("ABB", 2023, 400, CargadorVE.TipoConector.CCS2, CargadorVE.TipoCargador.RAPIDO_DC, 2, 2, 60.0, CargadorVE.Ubicacion.UNIVERSIDAD);
        CargadorVE c2 = new CargadorVE("Siemens", 2022, 220, CargadorVE.TipoConector.TIPO_2, CargadorVE.TipoCargador.MURAL, 1, 1, 22.0, CargadorVE.Ubicacion.CENTRO_COMERCIAL);
        CargadorVE c3 = new CargadorVE("Delta", 2024, 800, CargadorVE.TipoConector.CCS2, CargadorVE.TipoCargador.ULTRARRAPIDO, 2, 2, 150.0, CargadorVE.Ubicacion.ESTACION_SERVICIO);
        CargadorVE c4 = new CargadorVE("Wallbox", 2021, 220, CargadorVE.TipoConector.TIPO_2, CargadorVE.TipoCargador.MURAL, 1, 1, 11.0, CargadorVE.Ubicacion.RESIDENCIAL);
        CargadorVE c5 = new CargadorVE("Enel X", 2025, 22.0); // Reducido
        CargadorVE[] flota = {c1, c2, c3, c4, c5};

        // PASO 2: Sesión de carga sobre C1
        c1.setPotenciaActual(40);
        System.out.println("[P01] Potencia C1: " + c1.getPotenciaActual() + " kW");
        c1.aumentarPotencia(15);
        System.out.println("[P02] Potencia C1: " + c1.getPotenciaActual() + " kW");
        System.out.printf("[P03] Tiempo estimado: %.2f horas\n", c1.tiempoEstimadoCarga(66));
        System.out.print("[P04]"); c1.aumentarPotencia(10);
        c1.reducirPotencia(30);
        System.out.println("[P05] Potencia C1: " + c1.getPotenciaActual() + " kW");
        System.out.printf("[P06] Tiempo con pausas: %.2f horas\n", c1.tiempoEstimadoCarga(50, 2, 15));
        System.out.printf("[P07] Tiempo potencia programada: %.2f horas\n", c1.tiempoEstimadoCarga(50, 40.0));
        c1.aumentarPotencia();
        System.out.println("[P08] Potencia C1: " + c1.getPotenciaActual() + " kW");
        c1.aumentarPotencia(5, 3);
        System.out.println("[P09] Potencia C1: " + c1.getPotenciaActual() + " kW");
        System.out.print("[P10]"); c1.reducirPotencia(50);
        c1.cortarCarga();
        System.out.println("[P11] Potencia C1: " + c1.getPotenciaActual() + " kW");
        System.out.printf("[P12] Tiempo (P=0): %.2f horas\n", c1.tiempoEstimadoCarga(10));

        // PASO 3: Operaciones sobre el resto de la flota
        c2.setPotenciaActual(22);
        c3.setPotenciaActual(120);
        c4.aumentarPotencia(7.4);
        c5.aumentarPotencia(30); // Usará el validado de potenciaMaxima=22, será rechazado.
        System.out.println("[P13] Potencias finales -> C2: " + c2.getPotenciaActual() + "kW | C3: " + c3.getPotenciaActual() + "kW | C4: " + c4.getPotenciaActual() + "kW | C5: " + c5.getPotenciaActual() + "kW");

        // PASO 4: Estadísticas y validaciones
        int[] conteos = CargadorVE.contarPorTipo(flota);
        System.out.print("[P14] Conteo por tipo: ");
        for (int i = 0; i < conteos.length; i++) {
            System.out.print(CargadorVE.TipoCargador.values()[i] + "=" + conteos[i] + " ");
        }
        System.out.println();
        System.out.printf("[P15] Promedio potencia: %.2f kW\n", CargadorVE.promedioPotencia(flota));
        CargadorVE mayor = CargadorVE.mayorPotencia(flota);
        System.out.println("[P16] Mayor potencia: " + mayor.getFabricante() + " (" + mayor.getPotenciaMaxima() + " kW)");
        System.out.println("[P17] Excesos potencia contratada: " + CargadorVE.excesosDePotenciaContratada(flota));
        
        CargadorVE[] f1 = CargadorVE.filtrar(flota, CargadorVE.TipoConector.TIPO_2);
        CargadorVE[] f2 = CargadorVE.filtrar(flota, CargadorVE.TipoCargador.MURAL);
        CargadorVE[] f3 = CargadorVE.filtrar(flota, CargadorVE.Ubicacion.UNIVERSIDAD);
        System.out.println("[P18] TIPO_2: " + f1.length + " (" + listarFabricantes(f1) + ") | MURAL: " + f2.length + " (" + listarFabricantes(f2) + ") | UNIVERSIDAD: " + f3.length + " (" + listarFabricantes(f3) + ")");
        
        System.out.println("[P19] Valores C5:");
        c5.mostrar(false);
        
        CargadorVE copiaC3 = new CargadorVE(c3);
        System.out.println("[P20] Copia C3 -> Fabricante: " + copiaC3.getFabricante() + ", Potencia actual: " + copiaC3.getPotenciaActual() + "kW, Tam. Bitácora: " + copiaC3.getBitacora().size() + " | Total cargadores creados: " + CargadorVE.getTotalCargadores());
        
        System.out.println("[P21] C1 Detallado:");
        c1.mostrar(true);
        
        System.out.println("[P22] Valor contadorRegistros: " + CargadorVE.contadorRegistros);
        
        CargadorVE[] arregloNulos = {c1, null, c3};
        System.out.printf("[P23] Promedio con nulls: %.2f | Length contarPorTipo(null): %d\n", CargadorVE.promedioPotencia(arregloNulos), CargadorVE.contarPorTipo(null).length);

        // ==========================================
        // RUTA INDIVIDUAL SEGÚN LA CÉDULA
        // ==========================================
        int r = N % 4;
        System.out.println("\n--- [R] RUTA INDIVIDUAL (N=" + N + ", r=" + r + ") ---");
        ejecutarRuta(r, N, flota);

        // ==========================================
        // PARTE F: EXTENSIÓN PERSONALIZADA
        // ==========================================
        System.out.println("\n--- PARTE F: EXTENSIÓN PERSONALIZADA ---");
        int d1 = (N / 10) % 10;
        int d2 = N % 10;
        
        CargadorVE c6 = new CargadorVE(
            "USC-" + N,
            2015 + d2,
            (N % 2 == 0) ? 220 : 400,
            CargadorVE.TipoConector.values()[N % 5],
            CargadorVE.TipoCargador.values()[N % 6],
            (d1 % 3) + 1,
            (d2 % 4) + 1,
            20.0 + N,
            CargadorVE.Ubicacion.values()[N % 8]
        );

        System.out.println("[X01] N=" + N + " | d1=" + d1 + " | d2=" + d2);
        c6.mostrar(false);

        c6.setPotenciaActual(c6.getPotenciaMaxima() / 2.0);
        c6.aumentarPotencia(d2 + 5, d1 + 1);
        System.out.println("[X02] Potencia C6 final: " + c6.getPotenciaActual() + " kW");

        System.out.printf("[X03] Tiempo estimado (E=%d kWh): %.2f horas\n", (N + 10), c6.tiempoEstimadoCarga(N + 10));

        CargadorVE[] flotaExtendida = new CargadorVE[flota.length + 1];
        System.arraycopy(flota, 0, flotaExtendida, 0, flota.length);
        flotaExtendida[flota.length] = c6;
        System.out.println("[X04] flotaExtendida creada y C6 agregado en última posición.");

        int[] conteosExt = CargadorVE.contarPorTipo(flotaExtendida);
        System.out.print("[X05] Estadísticas Extendidas -> Tipos: [");
        for (int i=0; i<conteosExt.length; i++) { System.out.print(CargadorVE.TipoCargador.values()[i] + ":" + conteosExt[i] + " "); }
        System.out.printf("] | Promedio: %.2f kW | Mayor: %s | Excesos: %d\n", CargadorVE.promedioPotencia(flotaExtendida), CargadorVE.mayorPotencia(flotaExtendida).getFabricante(), CargadorVE.excesosDePotenciaContratada(flotaExtendida));

        System.out.println("[X06] Total Cargadores: " + CargadorVE.getTotalCargadores() + " | contadorRegistros: " + CargadorVE.contadorRegistros);
        c6.mostrar(true);
    }

    // --- MÉTODOS AUXILIARES Y RUTAS ---
    
    private static String listarFabricantes(CargadorVE[] arreglo) {
        StringBuilder sb = new StringBuilder();
        for (CargadorVE c : arreglo) {
            if (c != null) sb.append(c.getFabricante()).append(",");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "Ninguno";
    }

    private static void ejecutarRuta(int r, int N, CargadorVE[] flota) {
        switch (r) {
            case 0: // r = 0: cargadoresPorConectores
                int conectoresBuscados = (N % 3) + 1;
                System.out.println("Buscando cargadores con " + conectoresBuscados + " conectores:");
                boolean hayR0 = false;
                for (CargadorVE c : flota) {
                    if (c != null && c.getNumeroConectores() == conectoresBuscados) {
                        System.out.println(" - " + c.getFabricante() + " (" + c.getTipoCargador() + ")");
                        hayR0 = true;
                    }
                }
                if (!hayR0) System.out.println("No hay resultados.");
                break;
            case 1: // r = 1: promedioVoltajePorConector
                CargadorVE.TipoConector tc = CargadorVE.TipoConector.values()[N % 5];
                System.out.println("Promedio voltaje para conector " + tc + ":");
                double sumaVoltaje = 0; int countVoltaje = 0;
                for (CargadorVE c : flota) {
                    if (c != null && c.getTipoConector() == tc) {
                        sumaVoltaje += c.getVoltajeNominal(); countVoltaje++;
                    }
                }
                if (countVoltaje > 0) System.out.println(sumaVoltaje / countVoltaje + " V");
                else System.out.println("No existen cargadores de ese conector.");
                break;
            case 2: // r = 2: tipoMasFrecuente
                int[] conteos = CargadorVE.contarPorTipo(flota);
                int maxCount = -1;
                for (int count : conteos) if (count > maxCount) maxCount = count;
                System.out.println("Tipos más frecuentes (" + maxCount + " equipos):");
                for (int i = 0; i < conteos.length; i++) {
                    if (conteos[i] == maxCount && maxCount > 0) {
                        System.out.println(" - " + CargadorVE.TipoCargador.values()[i]);
                    }
                }
                break;
            case 3: // r = 3: sesionesSobreLimiteRed
                System.out.println("Sesiones sobre LIMITE_RED (" + CargadorVE.LIMITE_RED + " kW):");
                boolean hayR3 = false;
                for (CargadorVE c : flota) {
                    if (c != null) {
                        for (CargadorVE.RegistroSesion reg : c.getBitacora()) {
                            if (reg.isValido() && reg.getPotenciaActualReg() > CargadorVE.LIMITE_RED) {
                                System.out.println(" " + reg.describir());
                                hayR3 = true;
                            }
                        }
                    }
                }
                if (!hayR3) System.out.println("No hay casos.");
                break;
        }
    }
}