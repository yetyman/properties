import binding.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.yetyman.controls.GridHelper;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleBindingTests extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //one way binding test
        Property<Number> from = new Property<>(1);
        Property<Number> to = new Property<>(1);
        to.bind(from);
        //one way binding test 2
        Property<Number> from2 = new Property<>(1);
        Property<Number> to2 = new Property<>(1);
        from2.bindOut(to2);
        //two way binding test
        Property<Number> from3 = new Property<>(1);
        Property<Number> to3 = new Property<>(1);
        to3.bind(from3);
        from3.bind(to3);
        //two way easy binding test
        Property<Number> from4 = new Property<>(1);
        Property<Number> to4 = new Property<>(1);
        from4.bindDuplex(to4);
        //one way non property test
        Consumer<Double> consum = System.out::println;
        Random r = new Random();
        Supplier<Double> supp = r::nextDouble;
        OneWayBinding<Double> a = new OneWayBinding<>(supp::get);
        a.registerReceiver(consum::accept);
        //one way jfx property test
        //can't event off of the jfx property by default. can anything be done about that without adding a jfx dependency? (no)
        SimpleDoubleProperty jfxProperty = new SimpleDoubleProperty(1);
        SimpleDoubleProperty jfxProperty2 = new SimpleDoubleProperty(1);
        OneWayBinding<Double> a2 = new OneWayBinding<>(jfxProperty::get);
        a2.registerReceiver(jfxProperty2::set);
        jfxProperty.addListener(e->a2.push());

        //lazy jfx duplex binding test
        SimpleDoubleProperty jfxProperty3 = new SimpleDoubleProperty(1);
        SimpleDoubleProperty jfxProperty4 = new SimpleDoubleProperty(1);
        OneWayBinding<Double> a3 = new OneWayBinding<>(jfxProperty3::get, jfxProperty4::set);
        OneWayBinding<Double> a4 = new OneWayBinding<>(jfxProperty4::get, jfxProperty3::set);
        DuplexBinding<Double, Double> a34 = new DuplexBinding<>(a3, a4);

        //non-lazy jfx duplex binding test
        SimpleDoubleProperty jfxProperty5 = new SimpleDoubleProperty(1);
        SimpleDoubleProperty jfxProperty6 = new SimpleDoubleProperty(1);
        OneWayBinding<Double> a5 = new OneWayBinding<>(jfxProperty5::get, jfxProperty6::set);
        OneWayBinding<Double> a6 = new OneWayBinding<>(jfxProperty6::get, jfxProperty5::set);
        DuplexBinding<Double, Double> a56 = new DuplexBinding<>(a5, a6);
        jfxProperty5.addListener(e->a56.getA().push());
        jfxProperty6.addListener(e->a56.getB().push());

        //non-lazy jfx duplex binding test
        Property<Double> from50 = new Property<>(1d);
        SimpleDoubleProperty jfxProperty7 = new SimpleDoubleProperty(1);
        from50.bindOut(v->new Thread(()->Platform.runLater(()->jfxProperty7.set(v))).start());//just simulating changes off of the ui thread
        from50.bind(jfxProperty7::get);//lazy without hooking into the jfx listener.

        //always use newest test
        Property<Number> to5 = new Property<>(1){
            @Override
            protected void updateFromInwardBindings() {
                //clearing out this method to avoid lazily using bound values
            }
        };
        Property<Number> from5a = new Property<>(1);
        Property<Number> from5b = new Property<>(1);
        Property<Number> from5c = new Property<>(1);
        to5.bind(from5a);
        to5.bind(from5b);
        to5.bind(from5c);

        //only use earliest non-match when lazy test (Default)
        Property<Number> to6 = new Property<>(1);
        Property<Number> from6a = new Property<>(1);
        Property<Number> from6b = new Property<>(1);
        Property<Number> from6c = new Property<>(1);
        to6.bind(from6a);
        to6.bind(from6b);
        to6.bind(from6c);
        //one way conversion test
        Property<Double> from7 = new Property<>(1d);
        Property<String> to7 = new Property<>("1.3");
        OneWayConversionBinding<Double, String> doubleToStr = new OneWayConversionBinding<>(from7, String::valueOf, to7);
        from7.bindOut(doubleToStr);
        doubleToStr.push();
        //two way conversion test
        Property<Double> from8 = new Property<>(1d);
        Property<String> to8 = new Property<>("1.3");
        OneWayConversionBinding<String, Double> strToDouble2 = new OneWayConversionBinding<>(to8, Double::parseDouble, from8);
        OneWayConversionBinding<Double, String> doubleToStr2 = new OneWayConversionBinding<>(from8, String::valueOf, to8);
        to8.bindOut(strToDouble2);
        from8.bindOut(doubleToStr2);


        Label   l1 = new Label("One way binding(a->b  b.bind(a) )"),
                l2 = new Label("One way binding(a->b  a.bindOut(b) )"),
                l3 = new Label("Two way binding(a<->b  a.bind(b), b.bind(a) )"),
                l4 = new Label("Two way binding(a<->b  a.bindDuplex(b) )"),
                l5 = new Label("One way binding, no properties. producer=Random.nextDouble, consumer=System.out.println"),
                l6 = new Label("JavaFX properties bound one way without JavaFX.property.bind(...)"),
                l7 = new Label("JavaFX properties bound two way lazily without JavaFX.property.bindbidirectional(...), via binding.push()"),
                l8 = new Label("JavaFX properties bound two way without JavaFX.property.bindbidirectional(...)"),
                l9 = new Label("TwoWayBinding with Platform.RunLater invocation one way to JavaFX.property, lazy back(great for communicating system state)"),
                l10 = new Label("Multiple sources to one receiver. Always uses the newest value on get"),
                l11 = new Label("Multiple sources to one receiver. Always uses the earliest binding's value on get"),
                l12 = new Label("One way binding with type conversion"),
                l13 = new Label("Two way binding with type conversion");
        GridPane gp = new GridPane();
        gp.setGridLinesVisible(true);
        
        GridHelper.size(gp, 3,38);
        GridHelper.layout(gp,
                l1, l1, l1,
                createButton(from),   createLabel(from), createLabel(to),
                l2, l2, l2,
                createButton(from2), createLabel(from2), createLabel(to2),
                l3, l3, l3,
                createButton(from3), createLabel(from3), createGetButton(to3),
                createButton(to3), createLabel(to3), createGetButton(to3),
                l4, l4, l4,
                createButton(from4), createLabel(from4), createGetButton(to4),
                createButton(to4), createLabel(to4), createGetButton(to4),
                l5, l5, l5,
                createButton(a), null, new Label("Will print random"),
                l6, l6, l6,
                createButtonD(jfxProperty.asObject()), createLabel(jfxProperty), createGetButton(jfxProperty),
                createButtonD(jfxProperty2.asObject()), createLabel(jfxProperty2), createGetButton(jfxProperty2),
                l7, l7, l7,
                createButtonD(jfxProperty3.asObject()), createLabel(jfxProperty3), createButton(a4),
                createButtonD(jfxProperty4.asObject()), createLabel(jfxProperty4), createButton(a3),
                l8, l8, l8,
                createButtonD(jfxProperty5.asObject()), createLabel(jfxProperty5), createGetButton(jfxProperty5),
                createButtonD(jfxProperty6.asObject()), createLabel(jfxProperty6), createGetButton(jfxProperty6),
                l9, l9, l9,
                createButtonD(from50), createLabel(from50), createGetButton(from50),
                createButtonD(jfxProperty7.asObject()), createLabel(jfxProperty7), createGetButton(jfxProperty7),
                l10, l10, l10,
                createButton(from5a), createLabel(from5a), null,
                createButton(from5b), createLabel(from5b), createLabel(to5),
                createButton(from5c), createLabel(from5c), createGetButton(to5),
                l11, l11, l11,
                createButton(from6a), createLabel(from6a), null,
                createButton(from6b), createLabel(from6b), createLabel(to6),
                createButton(from6c), createLabel(from6c), createGetButton(to6),
                l12, l12, l12,
                createButtonD(from7), createLabel(from7), createGetButton(from7),
                null,                 createLabel(to7), createGetButton(to7),
                l13, l13, l13,
                createButtonD(from8), createLabel(from8), createGetButton(from8),
                createButtonS(to8),   createLabel(to8), createGetButton(to8)
                );

        var controller = gp;
//        controller.getStylesheets().add("./styles.css");
        controller.setPrefSize(600, 600);
//        controller.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        controller.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        this.primaryStage = primaryStage;
        this.primaryStage.setScene(new Scene(controller));
        primaryStage.show();
    }

    private Label createLabel(ISource<?> prop) {
        var label = new Label(prop.get()+"");
        prop.bindOut(v->label.setText(v+""));
        return label;
    }
    private Label createLabel(javafx.beans.property.Property<?> prop) {
        var label = new Label(prop.getValue()+"");
        label.textProperty().bind(Bindings.convert(prop));
        return label;
    }
    private Button createButton(IReceiver<Number> prop) {
        var button = new Button("+");
        if(prop instanceof IProperty<Number> p) {
            button.setOnAction(e -> p.set(p.get().doubleValue()+1));
        } else {
            AtomicInteger i = new AtomicInteger(1);
            button.setOnAction(e -> prop.set(i.incrementAndGet()));
        }
        return button;
    }
    private Button createGetButton(ISource<?> prop) {
        var button = new Button("=");
        button.setOnAction(e->prop.get());
        return button;
    }
    private Button createGetButton(javafx.beans.property.Property<?> prop) {
        var button = new Button("=");
        button.setOnAction(e->prop.getValue());
        return button;
    }
    private Button createButton(IBinding<?,?> binding) {
        var button = new Button("+");
        button.setOnAction(e->binding.push());
        return button;
    }
    private Button createButtonD(IReceiver<Double> prop) {
        var button = new Button("+");

        if(prop instanceof IProperty<Double> p) {
            button.setOnAction(e -> p.set(p.get() +1));
        } else {
            AtomicInteger i = new AtomicInteger(1);
            button.setOnAction(e -> prop.set((double) i.incrementAndGet()));
        }
        return button;
    }
    private Button createButtonD(IProperty<Double> prop) {
        var button = new Button("+");
        button.setOnAction(e->prop.set(prop.get()+1));
        return button;
    }
    private Button createButtonS(IProperty<String> prop) {
        var button = new Button("+");
        button.setOnAction(e->prop.set("11.2"));
        return button;
    }
    private Button createButtonD(javafx.beans.property.Property<Double> prop) {
        var button = new Button("+");
        button.setOnAction(e->prop.setValue(prop.getValue()+1));
        return button;
    }
}
