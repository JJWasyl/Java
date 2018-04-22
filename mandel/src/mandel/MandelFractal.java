package mandel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


//https://stackoverflow.com/questions/8381675/how-to-perform-simple-zoom-into-mandelbrot-set

interface ComplexDrawable{
    void draw(PixelWriter pw, Complex a, Complex b, int w, int h);
}


public class MandelFractal extends Complex implements ComplexDrawable{

    //Wartosci offesetowe i argumenty liczb zespolonych minimum i maximum
    private static final double OFFSET = -0.5;
    private double XMIN = -2.0, YMIN = -1.2, XMAX = 2.0, YMAX = 1.2;
    int max_it = 100; //Ile iteracji

    private GraphicsContext gc;

    @FXML
    public Canvas canvas;

    //Wymiary
    private int width = 900, height = 600;
    WritableImage wr = new WritableImage(width, height);
    PixelWriter pw = wr.getPixelWriter();

    //Zmienne potrzebne do "zoom"
    private double x1, y1, x2, y2;

    Complex minimum = new Complex(XMIN, YMIN);
    Complex maximum = new Complex(XMAX, YMAX);


    public void draw(PixelWriter pw, Complex a, Complex b, int w, int h) {

        //Konwersja skali pixelowej xy do układu wspolrzednych fraktalu
        double Re_factor = (a.re() - b.re())/(w-1);
        double Im_factor = (a.im() - b.im())/(h-1);
        Complex c = new Complex();

        //Petla przechodzaca po calym ukladzie wsp i malujaca kolejne pixele
        //Zidentyfikowac gdzie umiescic translacje moveX i moveY
        for(int xR = 0; xR < w; xR++){
            c.setRe((b.re() + (xR*Re_factor)));
            for(int yR = 0; yR < h; yR++){
                c.setIm((b.im() + (yR*Im_factor)));

                //Sprawdzamy zbieznosc funckji wartosc zbieznosci ustala kolor w fraktalu
                double convergenceValue = checkConvergence(c, max_it);
                double t1 = (double) convergenceValue / max_it;
                double c1 = Math.min(255*2*t1, 255); c1 = c1/255;
                double c2 = Math.max(255*(2*t1-1), 0); c2 = c2/255;
                if(convergenceValue != max_it){
                    pw.setColor(xR, yR, Color.color(c1, c2, c1));
                } else {
                    pw.setColor(xR, yR, Color.BLACK);
                }
            }

        }
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        gc.drawImage(wr, 0, 0, width, height);
    }

    //Metoda sprawdzajaca zbieznosc funkcji i zwracajaca iterator lub max_it
    private int checkConvergence(Complex a, int it){
        Complex z = new Complex(0.0, 0.0);
        for(int i = 0; i < it; i++){
            Complex zsqr = Complex.mul(z, z);
            z = Complex.add(zsqr, a);
            if((z.re()*z.re()) + (z.im()*z.im()) >= 4.0){
                return i;
            }
        }
        return it;
    }

    @FXML
    private TextField Width;
    @FXML
    private TextField Height;
    @FXML
    private TextField Real;
    @FXML
    private TextField Imaginary;
    @FXML
    private TextField Precision;

    //Przycisk Draw
    public void drawMand(ActionEvent actionEvent){

        //Jesli zmieniamy rodzielczosc
        if(Width != null && !Width.getText().isEmpty() && Height != null && !Height.getText().isEmpty()) {
            String temp1 = Width.getText();
            width = Integer.parseInt(temp1);
            String temp2 = Height.getText();
            height = Integer.parseInt(temp2);
            canvas.setWidth(Double.parseDouble(temp1));
            canvas.setHeight(Double.parseDouble(temp2));
            wr = new WritableImage(width, height);
            pw = wr.getPixelWriter();
        }
        //Jesli zmieniamy zakres funkcji
        if(Real != null && !Real.getText().isEmpty() && Imaginary != null && !Imaginary.getText().isEmpty()){
            String newreal = Real.getText();
            double re = Double.parseDouble(newreal);
            String newimg = Imaginary.getText();
            double img = Double.parseDouble(newimg);
            maximum.setVal(re, img);
            minimum.setVal(-re, -img);
        }
        //Jesli zmieniamy ilosc iteracji
        if(Precision != null && !Precision.getText().isEmpty()){
            String newp = Precision.getText();
            max_it = Integer.parseInt(newp);
        }

        draw(pw, maximum, minimum, width, height);
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        clear(gc);
    }

    public void mouseMoves(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        gc.setGlobalBlendMode(BlendMode.DIFFERENCE);
        gc.setStroke(Color.WHITE);
        rect(gc);
        x2 = x;
        y2 = y;

        rect(gc);
    }

    private void rect(GraphicsContext gc) {
        double x = x1;
        double y = y1;
        double w = x2 - x1;
        double h = y2 - y1;

        if (w < 0) {
            x = x2;
            w = -w;
        }

        if (h < 0) {
            y = y2;
            h = -h;
        }

        gc.strokeRect(x + 0.5, y + 0.5, w, h);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        x1 = mouseEvent.getX();
        y1 = mouseEvent.getY();
        x2 = x1;
        y2 = y1;
    }


    //Po zwolnieniu myszki zachodzi zoom
    public void mouseReleased(MouseEvent mouseEvent) {
        rect(gc);
        System.out.format("%f %f %f %f\n", x1, y1, x2, y2);

        //Zamiana wartosci x,y aby x1 i y1 byly minimum.
        if(x1 > x2){
            double temp = x2;
            x2 = x1;
            x1 = temp;
        }
        if(y1 > y2){
            double temp = y2;
            y2 = y1;
            y1 = temp;
        }

        //Jesli zaznaczenie za male, nic nie robimy
        if (x2 - x1 <= 10 || y2 - y1 <= 10) {
            return;
        }

        double xpix = (maximum.re() - minimum.re())/(width-1);
        double ypix = (maximum.im() - minimum.im())/(height-1);

        double x1p = x1*xpix + minimum.re();
        double x2p = x2*xpix + minimum.re();
        double y1p = y1*ypix + minimum.im();
        double y2p = y2*ypix + minimum.im();

        minimum.setVal(x1p, y1p);
        maximum.setVal(x2p, y2p);

        clear(gc);
        draw(pw, maximum, minimum, width, height);
    }


    //Kompletny reset obrazu i oczyszczenie pol tekstowych
    public void clearCanvas(ActionEvent actionEvent) {
        clear(gc);
        Width.clear(); Height.clear(); Real.clear(); Imaginary.clear(); Precision.clear();
        maximum.setVal(XMAX, YMAX);
        minimum.setVal(XMIN, YMIN);
    }


}
