int numBolas=2;

void setup(){
  size(1600,400);
  background(75);
}

void draw(){
  if(mousePressed){
    for(int i=0;i<numBolas;i++){
      genBall();
    }
  }else
    background(50);
}

void genBall(){
  color col = color(random(255),random(255),random(255));
  float radio = random(5,25);
  strokeWeight(2);
  stroke(255);
  fill(col);
  ellipse(pmouseX,pmouseY,radio*2,radio*2);
}
