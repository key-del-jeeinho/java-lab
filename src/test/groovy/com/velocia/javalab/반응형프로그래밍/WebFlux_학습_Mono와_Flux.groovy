package com.velocia.javalab.반응형프로그래밍

import reactor.core.publisher.Mono
import spock.lang.Specification

class WebFlux_학습_Mono와_Flux extends Specification{
    private final Random 난수 = new Random();
    /*
     모노 안에 값이 없으면 null 을 반환한다.

     모노는 여러번 block 이 가능하다. 즉 모노는 여러번 재사용이 가능하다.
     -> 이에 대한 정확한 문서를 찾아 보던 중, 다음과 같은 문구를 발견하였다
     -> Project Reactor streams are also reusable, which is a significant difference from Java 8 Streams.
     ->즉, Stream 과 달리 모노는 여러번 재사용이 가능하다.

     MonoJust 를 뜯어보니까 불변객체인듯하다 (value 가 final 객체)
     ->다만, deepcopy 를 하는지는 모르니 완전히 불변객체라 하긴 힘들지도

     여기서 생긴 궁금증, 모노.filter/map 같은 함수를 사용할 경우 새로운 모노 객체를 생성하는걸까?
     */
    def "스트림에서 작동하던 함수들을 Mono에서 동작시켜보기"() {
        given:
        Long 모노안에_들어갈_데이터 = 난수.nextLong()
        Mono<Long> 모노 = Mono<Long>.just(모노안에_들어갈_데이터 as Long)
        Long origin = 모노.block()
        Long filter = 모노.filter(x -> x > 200).block()
        Long filter2 = 모노.filter(x -> x <= 200).block()
        Long map = 모노.map(x -> x + 1).block()
        boolean 모노가_200보다_작은가 = 모노안에_들어갈_데이터 > 200;

        expect:
        모노안에_들어갈_데이터 == 모노.block()
        origin == 모노안에_들어갈_데이터
        filter == (모노가_200보다_작은가 ? 모노안에_들어갈_데이터 : null)
        filter2 == (모노가_200보다_작은가 ? null : 모노안에_들어갈_데이터)
        map == 모노안에_들어갈_데이터 + 1

        cleanup:
        "테스트 데이터 출력"(모노.block(), origin, filter, filter2, map)
    }

    /*
    모노에 filter map 같은 함수를 사용하면 새로운 모노객체를 만드는 듯 하다.
    -> - filter 사용시 MonoFilterFuseable 형식으로
    -> - map 사용시 MonoMapFuseable 형식으로 말이다.
    -> - 또한 just 로 Mono 를 생성하면 MonoJust 가 생성된다
    -> 세 자료형 모두 Mono 를 상속받았다.
     */
    def "모노의 filter map 같은 함수를 사용할 경우 새로운 모노 객체를 생성하는걸까"() {
        given:
        Long 모노안에_들어갈_데이터 = 난수.nextLong()
        Mono<Long> 모노 = Mono<Long>.just(모노안에_들어갈_데이터 as long)
        Mono<Long> 모노2 = 모노.filter(x -> true)
        Mono<Long> 모노3 = 모노.map(x -> x)
        "테스트 데이터 출력"(모노, 모노2, 모노3)

        expect:
        모노 != 모노2
        모노 != 모노3
    }

    static def "테스트 데이터 출력"(Object... data) {
        StringBuilder output = new StringBuilder('| ')
        for(Object datum : data) output.append(datum).append(' | ')
        print('테스트 데이터를 출력합니다!\n')
        print(output.toString().trim())
    }
}
