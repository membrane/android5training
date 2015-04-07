package de.predic8.meinebinderdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class MyDemoService extends Service {

    public class MyBinder extends IPrimeComputer.Stub {
        List<Long> primes = new ArrayList<>();
        long prime = 1;

        public void computeNextPrime(IOnPrimeComputedHandler callback) throws RemoteException {
            OUTER:
            while (true) {
                prime += 2;
                for (long prime2 : primes) {
                    if (prime % prime2 == 0)
                        continue OUTER;
                }
                break;
            }
            primes.add(prime);
            callback.onPrimeComputed(prime);
        }
    }

    MyBinder myBinder;

    public MyDemoService() {
        myBinder = new MyBinder();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
}
