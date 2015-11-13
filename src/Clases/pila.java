package Clases;

import java.util.ArrayList;


public class pila{

	private static ArrayList<coordenadas> p = new ArrayList<coordenadas>();
	private static int numero_elementos;
	/*public void vaciar()
	{
		while(!(p.isEmpty()))
		{
			coordenadas o = p.pop();
		}
	}*/
	public int get_numElementos()
	{
		return numero_elementos;
	}
	
	public void push(Object o, Object o1)
	{
		//Agregar valores a la pila
		coordenadas nuevo_nodo = new coordenadas(o, o1);
		p.add(nuevo_nodo);
		numero_elementos++;
	}
	public static coordenadas pop()
	{
		//Muestro ultimo valor y lo elimino
		if(!(p.isEmpty()))
		{
			coordenadas o = p.get(p.size()-1);
			p.remove(p.size()-1);
			numero_elementos--;
			return o;
		}
		else
		{
			return null;
		}
	}

	public Boolean empty()
	{
		return p.isEmpty();
	}
}

