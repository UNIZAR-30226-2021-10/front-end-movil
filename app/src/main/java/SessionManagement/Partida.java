package SessionManagement;

import java.util.Date;

public class Partida {

    private int idPartida;
    private Date fecha;
    private int numJugadores;
    private int rondas;
    private String ganador;

    public Partida(int idPartida, Date fecha, int numJugadores, int rondas, String ganador) {
        this.idPartida = idPartida;
        this.fecha = fecha;
        this.numJugadores = numJugadores;
        this.rondas = rondas;
        this.ganador = ganador;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getNumJugadores() {
        return numJugadores;
    }

    public int getRondas() {
        return rondas;
    }

    public String getGanador() {
        return ganador;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setNumJugadores(int numJugadores) {
        this.numJugadores = numJugadores;
    }

    public void setRondas(int rondas) {
        this.rondas = rondas;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }
}
