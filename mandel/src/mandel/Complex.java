package mandel;

import java.lang.Math;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Complex
{
    private double r, i;

    public Complex(){
        r = 0;
        i = 0;
    }
    public Complex(double real){
        r = real;
        i = 0;
    }
    public Complex(double real, double img){
        r = real;
        i = img;
    }
    public Complex(Complex c){
        r = c.r;
        i = c.i;
    }
    public Complex(String s){
        ArrayList <Double> liczby = new ArrayList <Double>();
        Matcher matcher = Pattern.compile("[+-]?\\d*\\.?\\d+([eE][+-]?\\d+)?").matcher(s);
        while(matcher.find()){
            double element = Double.parseDouble(matcher.group());
            liczby.add(element);
        }
        r = liczby.get(0);
        i = liczby.get(1);
    }

    public static Complex add(Complex a, Complex b){
        double real = a.r + b.r;
        double img = a.i + b.i;
        return new Complex(real, img);
    }

    public static Complex sub(Complex a, Complex b){
        double real = a.r - b.r;
        double img = a.i - b.i;
        return new Complex(real, img);
    }
    public static Complex mul(Complex a, Complex b){
        double real = a.r * b.r - a.i * b.i;
        double img = a.r * b.i + a.i * b.r;
        return new Complex(real, img);
    }
    public static Complex div(Complex a, Complex b){
        Complex conjugate = new Complex(b.r, -b.i);
        double licznik_real = a.r * conjugate.r - a.i * conjugate.i;
        double licznik_img = a.r * conjugate.i + a.i * conjugate.r;
        double mianownik = b.r * conjugate.r - b.i * conjugate.i;
        double real = licznik_real / mianownik;
        double img = licznik_img / mianownik;
        return new Complex(real, img);
    }

    public static double abs(Complex a){
        double abs = Math.hypot(a.i, a.r);
        return abs;
    }

    public static double phase(Complex a){
        double phase = Math.atan2(a.i, a.r);
        return phase;
    }

    public static double sqrAbs(Complex a){
        double abs = Math.hypot(a.i, a.r);
        return abs*abs;
    }

    public static double re(Complex a){
        return a.r;
    }
    public static double im(Complex a){
        return a.i;
    }


    /**
     * Poniższe metody modyfikuja aktualny obiekt i zwracają 'this'
     */

    public Complex add(Complex b){
        Complex a = this;
        double real = a.r + b.r;
        double img = a.i + b.i;
        return new Complex(real, img);
    }

    public Complex sub(Complex b){
        Complex a = this;
        double real = a.r - b.r;
        double img = a.i - b.i;
        return new Complex(real, img);
    }

    public Complex mul(Complex b){
        Complex a = this;
        double real = a.r * b.r - a.i * b.i;
        double img = a.r * b.i + a.i * b.r;
        return new Complex(real, img);
    }

    public Complex div(Complex b){
        Complex a = this;
        Complex conjugate = new Complex(b.r, -b.i);
        double licznik_real = a.r * conjugate.r - a.i * conjugate.i;
        double licznik_img = a.r * conjugate.i + a.i * conjugate.r;
        double mianownik = b.r * conjugate.r - b.i * conjugate.i;
        double real = licznik_real / mianownik;
        double img = licznik_img / mianownik;
        return new Complex(real, img);
    }

    public double abs(){
        double abs = Math.hypot(r, i);
        return abs;
    }

    public double sqrAbs(){
        double abs = Math.hypot(r, i);
        return abs * abs;
    }

    public double re(){
        return r;
    }

    public double im(){
        return i;
    }


    public String toString(){
        StringBuilder temp = new StringBuilder();
        if(i >= 0){
            temp.append(r).append("+").append(i).append("i");
        }
        else{
            temp.append(r).append("").append(i).append("i");
        }
        return temp.toString();
    }


    public static Complex valueOf(String s){
        Complex f = new Complex(s);
        ArrayList <Double> liczby = new ArrayList <Double>();
        Matcher matcher = Pattern.compile("[+-]?\\d*\\.?\\d+([eE][+-]?\\d+)?").matcher(s);
        while(matcher.find()){
            double element = Double.parseDouble(matcher.group());
            liczby.add(element);
        }
        f.r = liczby.get(0);
        f.i = liczby.get(1);
        return f;
    }

    public void setRe(double x){
        r = x;
    }

    public void setIm(double x){
        i = x;
    }

    public void setVal(Complex a){
        r = a.r;
        i = a.i;
    }

    public void setVal(double a, double b){
        r = a;
        i = b;
    }
}
