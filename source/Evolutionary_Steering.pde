import java.math.BigDecimal;

void setup() {
  size(1200, 700);
  frameRate(99999);
  for (int i = 0; i < 50; i ++) {
    rats.add(new Rat());
  }
  for (int i = 0; i < 10; i ++) {
    food.add(new Food());
  }
  //for (int i = 0; i < 80; i ++) {
  //  poison.add(new Poison());
  //}
}

void draw() {
  background(51);
  for (int i = 0; i < poison.size(); i++) {
    poison.get(i).update();
  }
  for (int i = 0; i < food.size(); i++) {
    food.get(i).update();
  }
  
  

  float[] advDNA = new float[rats.get(0).dna.length];
  for (int i = 0; i < rats.size(); i++) {
    for (int d = 0; d < rats.get(0).dna.length; d++) {
      advDNA[d] += rats.get(i).dna[d];
    }
    rats.get(i).update();
  }
  
  
  

  for (int i = 0; i < rats.get(0).dna.length; i++) {
    advDNA[i] /= rats.size();
  }
  
  
  
  
  
  
  if (display) {
    for (int i = 0; i < rats.size(); i++) {
      rats.get(i).draw();
    }
    for (int i = 0; i < food.size(); i++) {
      food.get(i).draw();
    }
    for (int i = 0; i < poison.size(); i++) {
      poison.get(i).draw();
    }
    
    if(ui){
      
    }
  }
}

public static float round(float d, int decimalPlace) {
  BigDecimal bd = new BigDecimal(Float.toString(d));
  bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
  return bd.floatValue();
}

void keyPressed() {
  if (keyCode == 32) {
    debug = !debug;
  }
  if (keyCode == 17) {
    ui = !ui;
  }
  if (keyCode == 16) {
    display = !display;
  }
}
