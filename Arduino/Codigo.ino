
char valor; // Almacena la informacion recibida
int notify = 8; // PIN 8 (Usado por LED y Buzzer)
int pirpin = 4; // PIN 4 (Usado por la señal del sensor PIR)
bool estadoPIR = true; // Si la monitorizacion de movimiento esta activa
bool estadoPANICO = false; // Si el modo panico esta activo

long unsigned int lowIn;
long unsigned int pause = 1000; // Una pausa de 1 segundo despues de parado el movimiento
boolean lockLow = true; // Evita reactivar la notificacion si es un movimiento continuo
boolean takeLowTime;

void setup() {
  pinMode(notify, OUTPUT);
  pinMode(pirpin, INPUT);
  Serial.begin(9600);
}

void loop() {
  PIRSensor();
  Panico();
  if(Serial.available() > 0)  // Verifica si hay datos para leer
  { // si los hay
    valor = Serial.read(); // Lee el dato y lo almacena
    if(valor == '0') { // Si es 0, entonces se desactiva la monitorizacion
      estadoPIR = false;
    } else if(valor == '1') { // Si es 1, entonces se activa la monitorizacion
      estadoPIR = true;
    }
    if(valor == '3') { // Si es 3, entonces se activa el modo panico
      estadoPANICO = true;
    } else if(valor == '4') { // Si es 4, entonces se desactiva el modo panico
      estadoPANICO = false;
    }
  }
}

void Panico() {
  if (estadoPANICO) { // Si el modo panico esta activo
    digitalWrite(notify, HIGH);
  } else {
    if (!estadoPIR) {
      digitalWrite(notify, LOW);
    }
  }
}

void PIRSensor() {
  if (estadoPIR) { // Si la monitorizacion esta activa
    if (digitalRead(pirpin) == HIGH) { // Si hay movimiento
      if (lockLow) {
          lockLow = false;
          digitalWrite(notify, HIGH); // Enciende el LED y el Buzzer
          Serial.print("5"); // Envia un 5 (para notificar al telefono) 
          delay(50);
      }
      takeLowTime = true;
    }
    if (digitalRead(pirpin) == LOW) { // Si no hay movimiento
      if (takeLowTime) {
          lowIn = millis();
          takeLowTime = false;
      }
      if (!lockLow && millis() - lowIn > pause) { // Si durante un tiempo no se ha detectado movimiento
          lockLow = true;
          digitalWrite(notify, LOW); // Apaga el LED y el Buzzer
          delay(50);
      }
    }
  }
}
