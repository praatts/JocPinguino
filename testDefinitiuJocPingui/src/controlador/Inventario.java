
package controlador;
import modelo.*;
import java.util.*;

public class Inventario {

	private int idInventario;
	private int dados;
	private int dadosR;
	private int dadosL;
	private int peces;
	private int bolasDeNieve;

	Random r = new Random();

	public Inventario(int idInventario, int dados, int dadosR, int dadosL, int peces, int bolasDeNieve) {
		this.idInventario = idInventario;
		this.dados = dados;
		this.dadosR = dadosR;
		this.dadosL = dadosL;
		this.peces = peces;
		this.bolasDeNieve = bolasDeNieve;
	}

	
	public int getIdInventario() {
		return idInventario;
	}


	public void setIdInventario(int idInventario) {
		this.idInventario = idInventario;
	}


	public int getDados() {
		return dados;
	}

	public void setDados(int dados) {
		this.dados = dados;

	}

	public int getPeces() {
		return peces;
	}

	public void setPeces(int peces) {
		this.peces = peces;
	}

	public int getBolasDeNieve() {
		return bolasDeNieve;
	}

	public void setBolasDeNieve(int bolasDeNieve) {
		this.bolasDeNieve = bolasDeNieve;
	}
	
	public int getDadosR() {
		return dadosR;
	}
	
	public void  setDadosR (int dadosR) {
		this.dadosR = dadosR;
	}
	
	public int getDadosL() {
		return dadosL;
	}
	
	public void setDadosL(int dadosL) {
		this.dadosL = dadosL;
	}
	
	public void obtenerPescado(Pinguino pingu) {

		if (pingu.getInventario().getPeces() < 2) {
			pingu.getInventario().setPeces(getPeces() + 1);
			if (pingu.getInventario().getPeces() == 1) {
				System.out.println("Has obtenido un pez! Tienes " + pingu.getInventario().getPeces() + " pez en el inventario");
			} else {
				System.out.println("Has obtenido un pez! Tienes " + pingu.getInventario().getPeces() + " peces en el inventario");
			}

		} else {
			System.out.println("Tienes el máximo de peces permitidos, no se ha añadido el pez al inventario.");
		}
		
	}

	public void obtenerBolasdeNieve(Pinguino pingu) {

		int generador = r.nextInt(3) + 1;

		// Muestra cuantas bolas se han obtenido.
		if (generador == 1) {
			System.out.println("Has obtenido una bola de nieve!");
		} else {
			System.out.println("Has obtenido " + generador + " bolas de nieve!");
		}

		// Verifica si tiene el máximo de bolas permitidas.

		if (pingu.getInventario().getBolasDeNieve() >= 6) {
			System.out.println("Tienes el máximo de bolas de nieve permitidas.");
		} else {
			// Calcula el total después de agregar las bolas generadas.
			int bolasTotales = pingu.getInventario().getBolasDeNieve() + generador;

			// Si el total excede el permitido lo actualiza a 6.
			if (bolasTotales >= 6) {
				pingu.getInventario().setBolasDeNieve(6);
				System.out.println("Tienes el máximo de bolas de nieve permitidas.");
			
		} else {

				// Si no excede el total se añaden las generadas y nada más.
				pingu.getInventario().setBolasDeNieve(bolasTotales);
			}

			if (pingu.getInventario().getBolasDeNieve() == 1) {
				System.out.println("Tienes 1 bola de nieve en el inventario.");
				
			} else {
				// Fuera del bucle y una vez actualizado se muestra el total de bolas
				System.out.println("Tienes " + pingu.getInventario().getBolasDeNieve() + " bolas de nieve en el inventario.");
			}

		}
		
	}


}
