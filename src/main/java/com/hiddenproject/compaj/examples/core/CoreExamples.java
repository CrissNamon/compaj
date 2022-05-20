package com.hiddenproject.compaj.examples.core;

import java.util.HashMap;
import java.util.Map;
import com.hiddenproject.compaj.core.data.base.RealFunction;
import com.hiddenproject.compaj.core.model.base.BaseModel;

public class CoreExamples {

  public CoreExamples() {

    //Создаем функцию с именем MyFunction
    RealFunction f = new RealFunction("MyFunction");
    //Описываем функцию f(x,y) = x+y
    f.b( x -> x[0] + x[1] );
    //Вычисляем функцию f(0,1) = 1
    f.value(new Double[]{});

    //Создаем модель с именем MyModel
    BaseModel model = new BaseModel("MyModel");
    //Добавляем функцию MyFunction в модель
    model.a(f);

    //Собираем аргументы для всех функций в модели
    Map args = new HashMap<String, Double[]>();
    args.put("MyFunction", new Double[]{0d, 1d});
    //Вычисляем все функции в модели от переданных аргументов
    model.compute(args);


    //Заменяем стандартный метод для вычисления модели
    model.bu(
        //Метод получает аргументы для функций в модели
        arguments -> {
      //Выводим названия всех функций, для которых были переданы аргументы
      arguments.keySet().forEach(System.out::println);
    });

  }

}
