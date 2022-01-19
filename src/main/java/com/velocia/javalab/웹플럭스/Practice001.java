package com.velocia.javalab.웹플럭스;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

public class Practice001 {
    public static void main(String[] args) {
        List<String> names1 = Arrays.asList("지인호", "최성현", "박민서", "정시원", "이선우", "테스트");
        List<String> names2 = Arrays.asList("이서린", "박민서", "오종진", "이선우", "백기선");
        List<String> names3 = Arrays.asList("지인호", "이현빈", "노경준", "정시원", "오혜윤", "안진우", "오종진");
        List<List<String>> names = Arrays.asList(names1, names2, names3);

        Flux<String> flux = Flux.fromIterable(names1);
        //js 콜백함수쓰는거같고 재밌네
        flux.subscribe(
                System.out::println,
                err -> System.out.println("오류발생 | " + err),
                () -> System.out.println("작업종료")
        ); //인자로 Consumer<T> 를 받는듯 (Flux<T>)
        flux.map(x -> {
            if(x.equals("테스트")) throw new RuntimeException("테스트용 오류");
            return "학생 - " + x;
        }).subscribe(
                System.out::println,
                err -> System.out.println("오류발생 | " + err),
                () -> System.out.println("작업종료")
        ); //인자로 ;

    }
}
