class Food {
  PVector position;
  PVector velocity = new PVector(0, 0);
  float fValue = foodValue;

  Food() {
    position = new PVector(random(width), random(height));
  }

  Food(Rat parent) {
    position = new PVector(parent.position.x, parent.position.y);
    this.velocity = parent.velocity;
  }

  void update() {
    position.add(velocity);
    boundries();
  }

  void draw() {
    if (display) {
      stroke(0, 150, 0);
      fill(0, 100, 0);
      ellipseMode(CENTER);
      ellipse(position.x, position.y, foodSize, foodSize);
    }
  }

  void boundries() {
    if (position.x > width) {
      position.x = 0;
    }
    if (position.x < 0) {
      position.x = width;
    }
    if (position.y > height) {
      position.y = 0;
    }
    if (position.y < 0) {
      position.y = height;
    }
  }
}
