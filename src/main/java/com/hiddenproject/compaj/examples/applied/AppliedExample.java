package com.hiddenproject.compaj.examples.applied;

import com.hiddenproject.compaj.core.data.base.RealFunction;
import com.hiddenproject.compaj.core.model.base.FirstOrderDifferentialModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

public class AppliedExample {

  public AppliedExample() {

    //Выбираем метод для решения дифференциальных уравнений
    FirstOrderIntegrator integrator = new EulerIntegrator(1);
    //Создаем модель из системы дифференциальных уравнений
    FirstOrderDifferentialModel difSIR = new FirstOrderDifferentialModel("SIR");
    difSIR.i(integrator);

    Double a = 0.04;
    Double b = 0.5;
    //Создаем уравнения
    RealFunction s = new RealFunction("S");
    RealFunction i = new RealFunction("I");
    RealFunction r = new RealFunction("R");
    //Описываем функции для вычисления
    s.b( x ->
        -b * difSIR.getAt("S") * difSIR.getAt("I")
            / (difSIR.getAt("S") + difSIR.getAt("I") + difSIR.getAt("R"))
    );
    i.b( x ->
        b * difSIR.getAt("S") * difSIR.getAt("I")
            / (difSIR.getAt("S") + difSIR.getAt("I") + difSIR.getAt("R"))
            - a * difSIR.getAt("I",1)
    );
    r.b( x ->
        a * difSIR.getAt("I")
    );
    //Вычисляем на отрезке t = [0,100]
    difSIR.compute(0d, 100d);

  }
}
