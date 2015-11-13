package Clases;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sin_raton extends JFrame implements ActionListener{

//Creación de variables de la Interfaz
	//Ejemplos: paneles, menús, botones, etc
	private JPanel contentPane;
	Container contenedor;
	JFrame panel_entrada;
	JPanel menu;
	JMenuBar mb,menu_empezar_juego;
	JMenu menu1,menu2;
	JMenuItem sin_mouse,con_mouse,salir,ayuda;
	JLabel fondoo;
	JTextField pirata_x,pirata_y,tesoro_x,tesoro_y,carga_obstaculos,numero_filas,numero_columnas;
	JButton mCasillas[][],aceptar_sin_mouse,opcion_empezar,opcion_ver_pila,nuevo_juego, ver_camino_minimo,quitar_obstaculo,cambiar_pirata,cambiar_tesoro;
	

//Creación de variables del juego	
	Boolean mCasillas_visitados[][],mCasillas_Obstaculos[][], control_bucles;

	//Filas y columnas de la matriz dónde se desarrolla el juego
	int num_filas,num_columnas,num_obstaculos;
	//Posiciones en la matriz para el pirata y el tesoro
	int posicion_x_pirata,posicion_y_pirata,posicion_x_tesoro,posicion_y_tesoro;
	//Posiciones auxiliares para el pirata cuando comienza a moverse en búsqueda del tesoro
	int posicion_x_pirata_aux;
	int posicion_y_pirata_aux;
	//Variables ancho y alto para configurar el tamaño de las imágenes en los botones
	int ancho,alto;
	//Creación de una pila para guardar los movimientos del pirata
	pila p = new pila();
	/**
	 * Create the frame.
	 */
	public Sin_raton() {

	}

	//Menú principal del juego. Desde éste podremos acceder a las dos modalidades: Sin ratón y Con ratón. En la primera los obstáculos se situarán aleatoriamente
	//mientras que en la segunda opción, tanto el pirata como el tesoro como los obstáculos podrán situarse en la matriz de botones mCasillas haciendo click en la posición deseada
	public void Menu()
	{
		panel_entrada = new JFrame(" Buscar el tesoro ");
		panel_entrada.setBounds(10,10,250,200);
		panel_entrada.setLocationRelativeTo(null);//
		panel_entrada.setResizable(false);

		contenedor= panel_entrada.getContentPane();

		ImageIcon fondo = new ImageIcon((getClass().getResource("/Images/fondo.jpg")));
		Image im_dimension= fondo.getImage();//Obtenemos el tamano de la imagen
		fondo = new ImageIcon (im_dimension.getScaledInstance(250,200,Image.SCALE_SMOOTH));

		fondoo= new JLabel(fondo);

		mb=new JMenuBar(); //Crea una barra de menu
		panel_entrada.setJMenuBar(mb);
		menu1=new JMenu("Inicio");//Crea boton opciones
		menu2=new JMenu("Ayuda");
		mb.add(menu1);//Lo anade a la barra 
		mb.add(menu2);
		sin_mouse=new JMenuItem("1 -Panel de datos");//Opcion sin raton. Me lleva a ventana para introducir coordenadas de pirata y tesoro mediante teclado 
		sin_mouse.addActionListener(this);
		menu1.add(sin_mouse);
		con_mouse=new JMenuItem("2 -Panel con raton");//Opcion con raton. Me lleva a ventana para introducir coordenadas de pirata y tesoro mediante el raton
		con_mouse.addActionListener(this);
		menu1.add(con_mouse);
		salir=new JMenuItem("3 -Salir");
		salir.addActionListener(this);
		menu1.add(salir);

		contenedor.add(fondoo);//Anadimos imagen al contenedor del panel
		panel_entrada.setSize(250,200);
		panel_entrada.setVisible(true);
		panel_entrada.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	//Interfaz para modalidad de juego sin ratón. 
	private void Interfaz_sin_raton() {
		// TODO Auto-generated method stub
		JFrame interfaz = new JFrame(" Búsqueda del tesoro ");
		interfaz.setBounds(10,10,250,200);
		interfaz.setResizable(false);
		interfaz.setVisible(true);
		interfaz.setSize(900,600);

		contenedor = interfaz.getContentPane();
		contenedor.setSize(900, 600);

		//Se crea un panel GridLayout redimensionado al numero de filas y columnas del tablero por el que se moverá el pirata
		JPanel panel_matriz = new JPanel(new GridLayout(num_filas,num_columnas));
		panel_matriz.setBounds(300, 100, 200, 200);
		panel_matriz.setSize(900,600);

		menu_empezar_juego=new JMenuBar();//Crea una barra de menu
		interfaz.setJMenuBar(menu_empezar_juego);
		
		//Crea el botón opcion_empezar y lo anado a la barra de menu
		opcion_empezar=new JButton("Empezar");
		opcion_empezar.addActionListener(this);
		menu_empezar_juego.add(opcion_empezar); 
		
		//Crea el botón Quitar Obstáculo y lo anado a la barra de menu
		quitar_obstaculo = new JButton("Quitar Obstaculo");
		quitar_obstaculo.addActionListener(this);
		menu_empezar_juego.add(quitar_obstaculo);
		
		//Crea el botón camino_minimo y lo anado a la barra de menu		
		ver_camino_minimo = new JButton("Mejor Camino");
		ver_camino_minimo.addActionListener(this);
		menu_empezar_juego.add(ver_camino_minimo);
		ver_camino_minimo.setVisible(false);
		
		//Elimina datos de la pila. Esto funciona como destructor, evitando problemas durante el juego en el caso de que cree varios mapas en la misma sesión, pudiendo quedar datos de partidas anteriores en la pila.
		while(!p.empty())
		{
			p.pop();
		}
		
		//Crea e inicializa la matriz mCasillas de tipo JButton. 
		mCasillas = new JButton[num_filas][num_columnas];
	
		//Se añade la matriz al panel y se inserta en cada uno de los botones una imagen inicial(arena)
		for(int i=0;i<num_filas;i++)
		{
			for(int j=0;j<num_columnas;j++)
			{
				mCasillas[i][j] = new JButton();

				//Determina valor del ancho y alto que deben tener las imagenes a la hora de insertarlas en el boton
				ancho = 900/num_columnas;
				alto = 600/num_filas;

				//Se obtiene la imagen de la arena con la que se decorará la matriz de botones de mCasillas
				ImageIcon imagen_arena = new ImageIcon(getClass().getResource("/Images/arena.jpg"));
				// Obtiene un icono a escala con las dimensiones especificadas a partir de las variables alto y ancho
				ImageIcon imagen_escalada= new ImageIcon(imagen_arena.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
				
				//A cada botón se le vincula unas coordenadas i,j
				String posicion = new Integer(i).toString();
				posicion += ","+new Integer(j).toString();
				mCasillas[i][j].setActionCommand(posicion);
				
				//Se añade al boton la imagen a escala de la arena
				mCasillas[i][j].setIcon(imagen_escalada);
				
				//Se añade al panel que contiene la matriz cada uno de los botones, con lo ya podran ser visualizados por el usuario
				panel_matriz.add(mCasillas[i][j]);
			}
		}	


		//Se añade la imagen del pirata en las coordenadas de la matriz correspondientes
		ImageIcon imagen_pirata = new ImageIcon(getClass().getResource("/Images/pirata.jpg"));
		ImageIcon imagen_escalada_pirata = new ImageIcon(imagen_pirata.getImage().getScaledInstance(ancho,alto,java.awt.Image.SCALE_SMOOTH));
		mCasillas[posicion_x_pirata][posicion_y_pirata].setIcon(imagen_escalada_pirata);
		panel_matriz.setVisible(true);
		
		//Se añade la imagen del tesoro en las coordenaas de la matriz correspondientes
		ImageIcon imagen_tesoro = new ImageIcon(getClass().getResource("/Images/tesoro.jpg"));
		ImageIcon imagen_escalada_tesoro = new ImageIcon(imagen_tesoro.getImage().getScaledInstance(ancho,alto,java.awt.Image.SCALE_SMOOTH));
		mCasillas[posicion_x_tesoro][posicion_y_tesoro].setIcon(imagen_escalada_tesoro);

		int aux = 0;

		//Se crea e inicializa la matriz de obstaculos. Esta matriz booleana indica donde se han ubicado los obstaculos.
		mCasillas_Obstaculos = new Boolean[num_filas][num_columnas];
		for(int i=0;i<num_filas;i++)
		{
			for(int j=0;j<num_columnas;j++)
			{
				mCasillas_Obstaculos [i][j]= false;
			}
		}
		//Se genera aleatoriamente los obstaculos y se añaden al boton correspondiente de la matriz
		while(aux < num_obstaculos)
		{

			Random r = new Random();
			int valorDado = r.nextInt(3)+1; 
			Random r1 = new Random();
			int pos_x_aleatoria = r1.nextInt(num_filas);

			Random r2 = new Random();
			int pos_y_aleatoria = r2.nextInt(num_columnas);

			//Se obtiene imagen de un obstaculo aletorio de entre los disponibles
			ImageIcon imagen_palmera = new ImageIcon(getClass().getResource("/Images/obstaculo"+valorDado+".jpg"));
			//Escalo la imagen del obstaculo al tamanio de los botones de la matriz mCasillas
			ImageIcon imagen_escalada = new ImageIcon(imagen_palmera.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
			
			//Se comprueba si la posicion aleatoria donde se pretende insertar el obstaculo no esta ocupada por el tesoro u otro pirata
			if((mCasillas_Obstaculos[pos_x_aleatoria][pos_y_aleatoria] == true)||(pos_x_aleatoria == posicion_x_pirata && pos_y_aleatoria == posicion_y_pirata) || (pos_x_aleatoria == posicion_x_tesoro && pos_y_aleatoria == posicion_y_tesoro))
			{
				//En este caso no se pone obstaculo
			}
			else
			{
				//Inserta en el boton correspondiente la imagen del obstaculo en la matriz mCasillas de tipo JButton
				mCasillas[pos_x_aleatoria][pos_y_aleatoria].setIcon(imagen_escalada);
				//Se indica en la matriz booleana de obstaculos que en esa posicion ahora existe uno
				mCasillas_Obstaculos[pos_x_aleatoria][pos_y_aleatoria] = true;
				aux++;
			}
			
		}
		
		//Se añade el panel al contenedor principal para que pueda ser visualizado por el usuario
		panel_matriz.setVisible(true);
		contenedor.add(panel_matriz);
	}
	
	//Gestión de eventos. Me permite realizar funciones y tareas cuando hago click en los distintos menús y botones de la interfaz
	public void actionPerformed(ActionEvent e) {//Resive las acciones de los botones
		//Accedo a modalidad de juego Con ratón.
		if(e.getSource() == con_mouse)
		{
			Con_raton juego_con_mouse = new Con_raton();
			juego_con_mouse.setVisible(true);
		}
		//Accedo a modalidad de juego Sin ratón
		if(e.getSource() == sin_mouse)
		{
			panel_entrada.remove(fondoo);
			panel_entrada.setSize(700,350);
			ImageIcon fondo = new ImageIcon((getClass().getResource("/Images/fondo.jpg")));
			Image im_dimension= fondo.getImage();//Obtenemos el tama�o de la imagen
			fondo = new ImageIcon (im_dimension.getScaledInstance(500,350,Image.SCALE_SMOOTH));

			JLabel fondoo= new JLabel(fondo);


			JPanel introduccion_datos = new JPanel(new GridLayout(9, 1));
			JLabel filas= new JLabel("Numero de filas de la matriz:") ;
			//coordx.setBounds(10,10,100,30);
			introduccion_datos.add(filas);
			numero_filas=new JTextField("7");//Creamos la caja(ventana) del texto
			introduccion_datos.add(numero_filas);//Anadimos la caja al panel
			JLabel columnas= new JLabel("Numero de columnas de la matriz:") ;
			introduccion_datos.add(columnas);
			numero_columnas=new JTextField("7");
			introduccion_datos.add(numero_columnas);
			JLabel pos_x_pirata=new JLabel("Coordenada x del pirata:");
			introduccion_datos.add(pos_x_pirata);

			pirata_x=new JTextField("1");//Creamos la caja(ventana) del texto

			introduccion_datos.add(pirata_x);//a�adimos la caja al panel
			JLabel pos_y_pirata=new JLabel("Coordenada y del pirata:");
			introduccion_datos.add(pos_y_pirata);
			pirata_y=new JTextField("1");//Creamos la caja(ventana) del texto
			introduccion_datos.add(pirata_y);//anadimos la caja al panel
			JLabel pos_x_tesoro=new JLabel("Coordenada x del tesoro:");
			introduccion_datos.add(pos_x_tesoro);
			tesoro_x=new JTextField("6");//Creamos la caja(ventana) del texto
			introduccion_datos.add(tesoro_x);//anadimos la caja al panel
			JLabel pos_y_tesoro=new JLabel("Coordenada y del tesoro:");
			introduccion_datos.add(pos_y_tesoro);
			tesoro_y=new JTextField("6");//Creamos la caja(ventana) del texto
			introduccion_datos.add(tesoro_y);//anadimos la caja al panel
			JLabel numero_obstaculos=new JLabel("Numero de objetos:");
			introduccion_datos.add(numero_obstaculos);
			carga_obstaculos=new JTextField("12");//Creamos la caja(ventana) del texto
			introduccion_datos.add(carga_obstaculos);//a�adimos la caja al panel

			JLabel label_vacio = new JLabel("");
			introduccion_datos.add(label_vacio);

			//Boton aceptar_sin_mouse
			aceptar_sin_mouse = new JButton("Aceptar");
			introduccion_datos.add(aceptar_sin_mouse);
			aceptar_sin_mouse.addActionListener(this);

			contenedor.add(fondoo);
			contenedor.add(introduccion_datos);
		}
		//Cuando hago click en el botón aceptar de la modalidad de juego sin ratón
		if(e.getSource() == aceptar_sin_mouse)
		{
			
			num_filas = Integer.parseInt(numero_filas.getText());
			num_columnas = Integer.parseInt(numero_columnas.getText());
			posicion_x_pirata = Integer.parseInt(pirata_x.getText());
			posicion_y_pirata = Integer.parseInt(pirata_y.getText());
			posicion_x_tesoro = Integer.parseInt(tesoro_x.getText());
			posicion_y_tesoro = Integer.parseInt(tesoro_y.getText());
			num_obstaculos = Integer.parseInt(carga_obstaculos.getText());

			Interfaz_sin_raton();
		}

		//Cuando hago click en el botón de quitar obstáculos, me elimina de la matriz mCasillas(tablero) dicho obstáculo, con lo cual el pirata podría ir a esa posición puesto que ya está libre
		if(e.getSource() == quitar_obstaculo)
		{
			JButton boton=(JButton)e.getSource();
			boton.setBackground(Color.lightGray);
			System.out.print("Quitar elemento");
			for(int i=0;i<num_filas;i++)
			{
				for(int j=0;j<num_columnas;j++)
				{
					mCasillas[i][j].addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e1)
						{
							JButton boton1 = (JButton)e1.getSource();

							String[] posicion_elemento = (boton1.getActionCommand()).split(",");
							int posicion_x_elemento = Integer.parseInt(posicion_elemento[0]);
							int posicion_y_elemento = Integer.parseInt(posicion_elemento[1]);
							if((posicion_x_elemento!=posicion_x_tesoro)||(posicion_y_elemento!=posicion_y_pirata)||(posicion_x_elemento!=posicion_x_tesoro)||(posicion_y_elemento!=posicion_y_tesoro))
							{
								boton1.setIcon(null);					
								ImageIcon imagen_arena = new ImageIcon(getClass().getResource("/Images/arena.jpg"));
								ImageIcon imagen_escalada = new ImageIcon(imagen_arena.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
								boton1.setIcon(imagen_escalada);	
								//Matriz de obstaculos en esta posicion como false. No hay obstaculo
								mCasillas_Obstaculos[posicion_x_elemento][posicion_y_elemento] = false;
							}
						}
					});
				}
			}
		}

		//Cuando hago click en el JButton "ver_camino_minimo", se me colorea en azul las posiciones por las que viajó el pirata únicamente una vez, sin producirse ciclos.
		if(e.getSource() == ver_camino_minimo)
		{
			JButton boton=(JButton)e.getSource();
			boton.setBackground(Color.lightGray);
			
			System.out.print("Camino minimo");
			int elementos_pila = 0;
			int contador = 0;
			int i=0,j=0;
				while(!p.empty())
				{
					coordenadas aux = p.pop();
					int pos_x_min = (int)aux.get_x();
					int pos_y_min = (int)aux.get_y();
					mCasillas[pos_x_min][pos_y_min].setIcon(null);
	
					ImageIcon imagen_bandera = new ImageIcon(getClass().getResource("/Images/bandera.jpg"));
					ImageIcon imagen_escalada3_1= new ImageIcon(imagen_bandera.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));			
					mCasillas[pos_x_min][pos_y_min].setIcon(imagen_escalada3_1);					
				}
		}
		//Cuando hago click en este botón, el pirata comienza a moverse por el tablero en búsqueda del tesoro
		if(e.getSource() == opcion_empezar)
		{
			JButton boton=(JButton)e.getSource();
			boton.setBackground(Color.lightGray);
			
			ver_camino_minimo.setVisible(true);
			
			posicion_x_pirata_aux = posicion_x_pirata;
			posicion_y_pirata_aux = posicion_y_pirata;
			
			//Pongo como primera posición en la pila la posición inicial del pirata
			p.push(posicion_x_pirata_aux,posicion_y_pirata_aux);
			
			mCasillas_visitados = new Boolean[num_filas][num_columnas];
			for(int i=0;i<num_filas;i++)
			{
				for(int j=0;j<num_columnas;j++)
				{
					mCasillas_visitados[i][j] = false;
				}
			}
			mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux] = true;

			control_bucles = false;
			//Utilizamos Runnable para que el camino se vaya dibujando poco a poco, permitiendo al usuario ver como se desplaza el pirata
			Runnable r1 = new Runnable()
			{
				public void run()
				{
					boolean control_bucles = false;
					try{
						while (((posicion_x_pirata_aux != posicion_x_tesoro)||(posicion_y_pirata_aux != posicion_y_tesoro))&&(control_bucles!=true))
						{
							Thread.sleep(250L);//TIEMPO EJECUCION
							
							int mov = calcular_heuristica();
							//movimientos[i] = mov;

							switch (mov){
							case 0:

								//Si la pila no está vacía
								if(p.empty()!=true)
								{		
									//LLamo a la pila y devuelvo las últimas coordenadas almacenadas
									coordenadas aux1 = new coordenadas();
									aux1 = p.pop();
									
									int posicion_pila_x = (int)aux1.get_x();
									int posicion_pila_y = (int)aux1.get_y();
									
									if((posicion_pila_x == posicion_x_pirata_aux-1)&&(posicion_pila_y == posicion_y_pirata_aux))
									{
										ImageIcon imagen_pirata3 = new ImageIcon(getClass().getResource("/Images/arriba_oscuro.jpg"));
										ImageIcon imagen_escalada3_1= new ImageIcon(imagen_pirata3.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
										mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada3_1);
									}
									if((posicion_pila_x == posicion_x_pirata_aux+1)&&(posicion_pila_y == posicion_y_pirata_aux))
									{
										ImageIcon imagen_pirata3 = new ImageIcon(getClass().getResource("/Images/abajo_oscuro.jpg"));
										ImageIcon imagen_escalada3_1= new ImageIcon(imagen_pirata3.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
										mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada3_1);
									}
									if((posicion_pila_x == posicion_x_pirata_aux)&&(posicion_pila_y==posicion_y_pirata_aux-1))
									{
										ImageIcon imagen_pirata3 = new ImageIcon(getClass().getResource("/Images/izquierda_oscuro.jpg"));
										ImageIcon imagen_escalada3_1= new ImageIcon(imagen_pirata3.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
										mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada3_1);
									}
									if((posicion_pila_x == posicion_x_pirata_aux)&&(posicion_pila_y == posicion_y_pirata_aux+1))
									{
										ImageIcon imagen_pirata3 = new ImageIcon(getClass().getResource("/Images/derecha_oscuro.jpg"));
										ImageIcon imagen_escalada3_1= new ImageIcon(imagen_pirata3.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
										mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada3_1);
									}
									System.out.print("\nPosicion desde la pila_x:"+posicion_pila_x);
									System.out.print("\nPosicion desde la pila_y:"+posicion_pila_y);
									//Pongo a true la posición en la que estoy
									mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux] = true;
									//Actualizo la posición del pirata al top de la pila
									posicion_x_pirata_aux = posicion_pila_x;
									posicion_y_pirata_aux = posicion_pila_y;
								}else
								{
									control_bucles = true;
								}
								break;
							case 1:

								ImageIcon imagen_pirata = new ImageIcon(getClass().getResource("/Images/arriba_oscuro.jpg"));
								ImageIcon imagen_escalada= new ImageIcon(imagen_pirata.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
								mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada);	
								mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux]=true;
								p.push(posicion_x_pirata_aux, posicion_y_pirata_aux);

								posicion_x_pirata_aux = posicion_x_pirata_aux-1;

								break;
							case 2:
								
								//Actualizo matriz visitados
								mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux]=true;

								ImageIcon imagen_pirata1 = new ImageIcon(getClass().getResource("/Images/abajo_oscuro.jpg"));
								ImageIcon imagen_escalada1_1= new ImageIcon(imagen_pirata1.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
								mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada1_1);

								//Inserto posición actual del pirata en la pila
								p.push(posicion_x_pirata_aux, posicion_y_pirata_aux);

								//Actualizo la posición del pirata 
								posicion_x_pirata_aux = posicion_x_pirata_aux+1;

								break;
							case 3:
								//Actualizo matriz visitados
								mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux]=true;

								ImageIcon imagen_pirata2 = new ImageIcon(getClass().getResource("/Images/izquierda_oscuro.jpg"));
								ImageIcon imagen_escalada2_1= new ImageIcon(imagen_pirata2.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
								mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada2_1);

								//Inserto posición actual del pirata en la pila
								p.push(posicion_x_pirata_aux, posicion_y_pirata_aux);

								//Actualizo la posición del pirata 
								posicion_y_pirata_aux = posicion_y_pirata_aux - 1;

								break;
							case 4:
								//Actualizo matriz visitados
								mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux]=true;

								ImageIcon imagen_pirata3 = new ImageIcon(getClass().getResource("/Images/derecha_oscuro.jpg"));
								ImageIcon imagen_escalada3_1= new ImageIcon(imagen_pirata3.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
								mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada3_1);

								//Inserto posición actual del pirata en la pila
								p.push(posicion_x_pirata_aux, posicion_y_pirata_aux);

								//Actualizo la posición del pirata 
								posicion_y_pirata_aux = posicion_y_pirata_aux + 1;
								break;
							}
							
							//Compruebo si la posición del pirata sigue siendo distinta a la del tesoro y ya no tengo mas posiciones en la pila con lo que control_bucles = true
							if((control_bucles!=false)&&((posicion_x_pirata_aux != posicion_x_tesoro) ||(posicion_y_pirata_aux != posicion_y_tesoro)))
							{
								if(calcular_heuristica() == 0)
								{
									//Se lanza un mensaje de aviso al usuario, indicandole que no hay camino posible para que el pirata alcance el objetivo
									JOptionPane.showMessageDialog(null, "No hay camino posible", "Fin del juego", JOptionPane.WARNING_MESSAGE);							
									break;
								}
							}
							//En el caso de que la posición del pirata sea igual a la del tesoro el juego termina y se lanza un mensaje de aviso al usuario indicándole que el tesoro ha sido encontrado
							if((posicion_x_pirata_aux==posicion_x_tesoro)&&(posicion_y_pirata_aux==posicion_y_tesoro))
							{
								ImageIcon imagen_pirata_tesoro = new ImageIcon(getClass().getResource("/Images/pirata_tesoro.jpg"));
								ImageIcon imagen_escalada= new ImageIcon(imagen_pirata_tesoro.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_FAST));
								mCasillas[posicion_x_pirata_aux][posicion_y_pirata_aux].setIcon(imagen_escalada);

								JOptionPane.showMessageDialog(null, "Tesoro encontrado", "Fin del juego", JOptionPane.WARNING_MESSAGE);	
								ver_camino_minimo.setVisible(true);
								quitar_obstaculo.setVisible(false);
								break;
							}
						}
					}
					catch(Exception e) {
						//e.printStackTrace();
					}
				}
			};
			Thread thr1 = new Thread(r1);
			thr1.start();	
		}
	}

	//Función para calcular las distancia euclídea entre las casillas adyacentes al pirata y la posición del tesoro
	private int calcular_heuristica() {
		// TODO Auto-generated method stub

		double distancia = 10000;
		int movimiento = 0;
		boolean control = false;

	//Para cada uno de los posibles movimientos, se calcula la distancia euclídea y compruebo si es menor que la distancia que tenemos ya almacenada	
		if(distancia > (Math.sqrt(Math.pow((posicion_x_pirata_aux-1)-posicion_x_tesoro,2)+Math.pow(posicion_y_pirata_aux-posicion_y_tesoro,2))) && (posicion_x_pirata_aux-1>=0))
		{
			if((mCasillas_visitados[posicion_x_pirata_aux-1][posicion_y_pirata_aux]==false) && (mCasillas_Obstaculos[posicion_x_pirata_aux-1][posicion_y_pirata_aux]==false))
			{
				distancia = Math.sqrt(Math.pow((posicion_x_pirata_aux-1)-posicion_x_tesoro,2)+Math.pow(posicion_y_pirata_aux-posicion_y_tesoro,2));
				movimiento = 1;		//movimiento arriba
				control = true;
			}
		}
	
		if(distancia > (Math.sqrt(Math.pow((posicion_x_pirata_aux+1)-posicion_x_tesoro,2)+Math.pow(posicion_y_pirata_aux-posicion_y_tesoro,2))) && (posicion_x_pirata_aux+1<=num_filas-1))
		{
			if((mCasillas_visitados[posicion_x_pirata_aux+1][posicion_y_pirata_aux]==false) && (mCasillas_Obstaculos[posicion_x_pirata_aux+1][posicion_y_pirata_aux]==false))
			{
				distancia = Math.sqrt(Math.pow((posicion_x_pirata_aux+1)-posicion_x_tesoro,2)+ Math.pow(posicion_y_pirata_aux-posicion_y_tesoro,2));
				movimiento = 2;		//movimiento abajo
				control = true;
			}
		}
		if(distancia > (Math.sqrt(Math.pow(posicion_x_pirata_aux-posicion_x_tesoro,2)+Math.pow((posicion_y_pirata_aux-1)-posicion_y_tesoro,2))) && (posicion_y_pirata_aux-1>=0))
		{
			if((mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux-1]==false) && (mCasillas_Obstaculos[posicion_x_pirata_aux][posicion_y_pirata_aux-1]==false))
			{
				distancia = Math.sqrt(Math.pow(posicion_x_pirata_aux-posicion_x_tesoro,2)+Math.pow((posicion_y_pirata_aux-1)-posicion_y_tesoro,2));
				movimiento = 3;		//movimiento izquierda
				control = true;
			}
		}
		if(distancia > (Math.sqrt(Math.pow(posicion_x_pirata_aux-posicion_x_tesoro,2)+Math.pow((posicion_y_pirata_aux+1)-posicion_y_tesoro,2))) && (posicion_y_pirata_aux+1<=num_columnas-1))
		{
			if((mCasillas_visitados[posicion_x_pirata_aux][posicion_y_pirata_aux+1]==false) && (mCasillas_Obstaculos[posicion_x_pirata_aux][posicion_y_pirata_aux+1]==false))
			{
				distancia = Math.sqrt(Math.pow(posicion_x_pirata_aux-posicion_x_tesoro,2)+Math.pow((posicion_y_pirata_aux+1)-posicion_y_tesoro,2));
				movimiento = 4;		//movimiento derecha
				control = true;
			}
		}
		
		//No se ha accedido a ningún if. Esto quiere decir que no es posible el movimiento del pirata
		if (control == false)
		{
			movimiento = 0;
		}
		return movimiento;
	}
}

