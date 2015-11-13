package Clases;

import Clases.Sin_raton;

//Clase principal main. Se crea un objeto de tipo Sin_raton(), el cual nos lanzará una ventana dónde accederemos al menu principal de la aplicación.
public class Main {
	int i;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sin_raton menu_principal = new Sin_raton();
		menu_principal.Menu();
	}

}