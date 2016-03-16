import processing.core.PApplet;
import processing.core.PFont;
import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Miguel Mendoza
 */
public class Laberinto extends PApplet{

	/*Atributos para la interfaz*/
	int altoTablero=35;   //Se puede modificar el alto
	int anchoTablero=60;  //y ancho del tablero.
	boolean pintarCuadricula,pintarLaberinto;

	/*Atributos del modelo*/
	private Celda[][]tablero; //Tablero
	private Random random; 
	private Stack<Celda> anteriores; //pila para backtracking
	private Celda actual; //La celda actual
	private Direccion dir; //Direcciones para borrar paredes

	/**/
	public void settings(){
		size((anchoTablero*20),(altoTablero*20));
	}

	 /** 
	 * Configuracion inicial 
	 */
    @Override
    public void setup(){
    	/*Llenar tablero de celdas*/
    	int x=0,y=0;
    	tablero = new Celda[anchoTablero][altoTablero];
    	for(int i=0;i<anchoTablero;i++){
    		for (int j=0;j<altoTablero;j++){
    			tablero[i][j] = new Celda(x,y,i,j);
    			y+=20;
    		}
    		x+=20;
    		y=0;
    	}
    	pintarCuadricula=true;
    	pintarLaberinto=false;
    	random = new Random();	
    	anteriores = new Stack<Celda>();    
    	actual = tablero[random.nextInt(anchoTablero)][random.nextInt(altoTablero)];
    	actual.visitado=true;
    	anteriores.push(actual);
    	/*Configuración interfaz*/
    	stroke(0);
    	strokeWeight(1);
    	background(255);          
    }

    @Override
    public void draw(){  
    	if(pintarCuadricula){
    		for (int i=0;i<anchoTablero;i++)
    			for (int j=0;j<altoTablero;j++)
    				rect(tablero[i][j].x,tablero[i][j].y,20,20);    	      	
    		pintarCuadricula=false;
    		pintarLaberinto=true;
    		return;
    	}    	
    	if(pintarLaberinto)
    		if(!anteriores.empty()){
    			Celda siguiente = getSiguiente(actual.i,actual.j);    		   		    			
	    		if(siguiente != null){
    				siguiente.visitado=true;
    				quitaPared(actual,siguiente);
	    			anteriores.push(siguiente);    				    			
    				actual = siguiente;    				
    			}else
    				actual=anteriores.pop();    		    		
 			}else{
 				pintarLaberinto=false;
 				noLoop();
 			}
 			//noLoop();		
    }

     /** Indica que se desea expandir el siguiente 
      * nivel al hacer click, se deben agregar loop() en este método
      * y noLoop() en draw(). 
      */
    @Override
    public void mouseClicked() {
        //loop();
    }

    /*Quita una pared entre dos celdas.*/
    private void quitaPared(Celda actual,Celda siguiente){
    	stroke(0,255,51);
    	strokeWeight(2);
    	if(dir==Direccion.ARRIBA)
    		line(actual.x,actual.y,actual.x+20,actual.y);    		
    	else if(dir==Direccion.ABAJO)
    		line(actual.x,actual.y+20,actual.x+20,actual.y+20);    		
    	else if(dir==Direccion.DER)
    		line(actual.x+20,actual.y,actual.x+20,actual.y+20);
    	else
    		line(actual.x,actual.y,actual.x,actual.y+20); 
    	noStroke();
    	fill(0,255,51);
    	rect(actual.x+1,actual.y+1,18,18);    	
    	rect(siguiente.x+1,siguiente.y+1,18,18);  
    }

    /* Obtiene la siguiente celda que no ha sido visitada e indica
	 * en que direccion se mueve en dir.
     */
    private Celda getSiguiente(int x,int y){
    	ArrayList<Celda> vecinos = getVecinos(x,y);
       	for(Celda c: vecinos)
    		if(!c.visitado){
    			if(x==c.i && y==c.j-1)
    				dir= Direccion.ABAJO;
    			else if(x==c.i && y==c.j+1)
    				dir=Direccion.ARRIBA;	
    			else if(x==c.i+1 && y==c.j)
    				dir=Direccion.IZQ;
    			else
    				dir=Direccion.DER;
    			return c;
    		}
    	return null;   				
    }

    /* Obtiene los vecinos de una celda dadas sus coordenadas,
     * los devuelve en orden aleatorio.
     */
    private ArrayList<Celda> getVecinos(int x, int y){
    	/*Checar límites para obtener los vecinos*/
    	int limIzq = (x-1<0)? x: x-1;
    	int limDer = (x+1==anchoTablero)? x: x+1;
    	int limArriba = (y-1<0)? y: y-1;
    	int limAbajo = (y+1==altoTablero)? y: y+1;
    	/*Obtenemos solo los vecinos que tienen una pared en común con la celda actual*/
    	ArrayList<Celda> vecinos = new ArrayList<Celda>(); 
    	for (int i=limIzq,n=0;i <= limDer;i++)
    		for (int j=limArriba;j <= limAbajo;j++)
    			if(!(i==x-1&&j==y-1)&&!(i==x+1&&j==y+1)&&!(i==x-1&&j==y+1)&&!(i==x+1&&j==y-1)&&!(i==x&&j==y))
    				vecinos.add(tablero[i][j]);
    	/*Shuffle!*/
    	Collections.shuffle(vecinos);
    	return vecinos;
    } 

	/**
	 * Modela una celda del tablero.
	 */
	private class Celda {
		
		/*Coordenadas para pintar*/		
		int x,y;
		/*Índices en la matriz(tablero)*/
		int i,j;
		/*Si ya fue visitada*/
		boolean visitado;

		/**
		 * Recibe las coordenadas de la celda y sus índices.
		 * @param x la coordenada x para pintar.
		 * @param y la coordenada y para pintar.
		 * @param i ínidice i.
		 * @param j Índice j.
		 */
		public Celda(int x,int y,int i,int j){
			visitado=false;
			this.x=x;
			this.y=y;
			this.i=i;
			this.j=j;
		}
	}

	/**
	 * Indica la dirección del vecino para poder borrar las paredes.
	 */
	private enum Direccion {
		ARRIBA,
		ABAJO,
		IZQ,
		DER
	}

	/**/
	static public void main(String args[]) {
        PApplet.main(new String[] { "Laberinto" });
    }

}

