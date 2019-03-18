Drop [] drops = new Drop [500];

void setup(){
   size(640, 360);
   for (int i=0; i < drops.length; i++){
       drops[i] = new Drop();
   }
}

void draw(){
  background(230, 230, 250);
  for (int i=0; i < drops.length; i++){
       drops[i].fall();
       drops[i].show();
   }
}

class Drop{
  
 float x = random(width);
 float y = random(-500, -50);
 float z = random(0, 20);
 float yspeed = map(z, 0, 20, 1, 10);
 float len = map(z, 0, 20, 10, 20);
 
 void fall(){
   y = y + yspeed;
   float grav = map(z, 0, 20, 0, 0.2);
   yspeed += grav;
   if(y > height){
      y =  random(-500, -50);
      yspeed = map(z, 0, 20, 4, 10);
   }
 }
  
  void show(){
   float thick = map(z, 0, 20, 1, 3);
   strokeWeight(thick);
   stroke(138, 43, 226); 
   line(x,y,x,y+len);
  }
  
}
