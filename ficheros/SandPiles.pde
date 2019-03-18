int[][] pilaArena;

void setup(){
  size(1200,400);
  pilaArena = new int[width][height];
  //pilaArena[width/2][height/2] = 1000000;
}

void draw(){
  if(mousePressed)
    dropSand();
  render();
  transmisionArena();
}

void dropSand(){
  int arenaSoltada=2048;
  if(pmouseX>0 && pmouseX<width && pmouseY>0 && pmouseY<height)
    pilaArena[pmouseX][pmouseY]=arenaSoltada;
}

void render(){
  loadPixels();
  for(int x=0;x<width;x++){
    for(int y=0;y<height;y++){
      int num = pilaArena[x][y];
      color col = color(0,31,198);
      if(num==0){
        col = color(252,241,115);
      }else if(num==1){
        col = color(39,240,228);
      }else if(num==2){
        col = color(15,208,240);  
      }else if(num==3){
        col = color(71,91,201);
      }
      pixels[x+y*width]=col;
    }
  }
  updatePixels();
}

void transmisionArena(){
  int[][] siguientePila = new int[width][height];
  for(int x=0;x<width;x++){
    for(int y=0;y<height;y++){
      int num = pilaArena[x][y];
      if(num < 4){
         siguientePila[x][y] = pilaArena[x][y];
      }
    }
  }
  for(int x=0;x<width;x++){
    for(int y=0;y<height;y++){
      int num = pilaArena[x][y];
      if(num >= 4){
        siguientePila[x][y] += (num - 4);
        if(x+1<width)
          siguientePila[x+1][y]++;
        if(x-1>0)
          siguientePila[x-1][y]++;
        if(y+1<height)
          siguientePila[x][y+1]++;
        if(y-1>0)
          siguientePila[x][y-1]++;
      }
    }
  }
  pilaArena = siguientePila;
}
