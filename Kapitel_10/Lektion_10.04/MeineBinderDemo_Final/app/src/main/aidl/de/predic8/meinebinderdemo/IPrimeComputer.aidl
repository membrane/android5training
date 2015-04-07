package de.predic8.meinebinderdemo;

import de.predic8.meinebinderdemo.IOnPrimeComputedHandler;

interface IPrimeComputer {
    void computeNextPrime(IOnPrimeComputedHandler callback);
}