package controlador;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Dado {

	Random r = new Random();
	Scanner s = new Scanner(System.in);

	private String tipo;
	private int valor;

	public Dado(String tipo, int valor) {
		this.tipo = tipo;
		this.valor = valor;
	}

	public String getTipoDado() {
		return tipo;
	}

	public int getValorDado() {
		return valor;
	}

	public void setValorDado(int valor) {
		this.valor = valor;
	}

	public void setTipoDado(String tipo) {
		this.tipo = tipo;
	}

	public Dado lanzar(Dado dadoActual) {
		int tipoDado = 0;
		String tipo = "";
		int valor = 0;
		boolean opcionValida = false;

		System.out.println(
				"Seleccione el tipo de dado que desea utilizar\n'1' para usar el dado NORMAL (1 a 6)\n'2' para usar el dado RÁPDIO (5 a 10) \n'3' para usar el dado LENTO (1 a 3)");
		while (!opcionValida) {
			try {
				tipoDado = s.nextInt();

				switch (tipoDado) {
				case 1:
					tipo = "Normal";
					valor = r.nextInt(6) + 1;
					System.out.println("Se ha lanzado el dado normal! Ha salido: " + valor);
					opcionValida = true;
					break;

				case 2:
					tipo = "Rápido";
					valor = r.nextInt(6) + 5;
					System.out.println("Se ha lanzado el dado rápido! Ha salido: " + valor);
					opcionValida = true;
					break;

				case 3:
					tipo = "Lento";
					valor = r.nextInt(3) + 1;
					System.out.println("Se ha lanzado el dado lento! Ha salido: " + valor);
					opcionValida = true;
					break;
				default:
					System.out.println(
							"La selección introducida no corresponde a ningún dado, por favor, introduzca una opción válida");
					break;
				}

			} catch (InputMismatchException e) {
				System.out.println("Ingrese un número para seleccionar el dado que desea utilizar");
			}

		}

		dadoActual.setTipoDado(tipo);
		dadoActual.setValorDado(valor);

		return dadoActual;
	}


}
