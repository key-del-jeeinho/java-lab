package com.velocia.javalab.반응형프로그래밍

import reactor.core.publisher.Mono
import spock.lang.Specification

class WebFlux_학습_Mono와_Flux extends Specification{
    def "모노로 놀아보기 (스트림에서 작동하던 함수들 동작시켜보기)"() {
        /**
         모노 안에 값이 없으면 null 을 반환한다.

         모노는 여러번 block 이 가능하다. 즉 모노는 여러번 재사용이 가능하다.
         -> 이에 대한 정확한 문서를 찾아 보던 중, 다음과 같은 문구를 발견하였다
         -> Project Reactor streams are also reusable, which is a significant difference from Java 8 Streams.
         ->즉, Stream 과 달리 모노는 여러번 재사용이 가능하다.

         MonoJust 를 뜯어보니까 불변객체인듯하다 (value 가 final 객체)
         ->다만, deepcopy 를 하는지는 모르니 완전히 불변객체라 하긴 힘들지도

         여기서 생긴 궁금증, 모노.filter/map 같은 함수를 사용할 경우 새로운 모노 객체를 생성하는걸까?
         */
        given:
        Long 모노안에_들어갈_데이터 = 231
        Mono<Long> 모노 = Mono<Long>.just(모노안에_들어갈_데이터 as Long)
        Long origin = 모노.block()
        Long filter = 모노.filter(x -> x > 200).block()
        Long filter2 = 모노.filter(x -> x < 200).block()
        Long map = 모노.map(x -> x + 1).block()

        expect:
        모노안에_들어갈_데이터 == 모노.block()
        origin == 모노안에_들어갈_데이터
        filter == 모노안에_들어갈_데이터
        map == 모노안에_들어갈_데이터 + 1
        filter2 == null

        cleanup:
        "테스트 데이터 출력"(모노.block(), origin, filter, filter2, map)
    }

    static def "테스트 데이터 출력"(Object... data) {
        StringBuilder output = new StringBuilder()
        for(Object datum : data) output.append(datum).append(' ')
        print('테스트 데이터를 출력합니다!')
        print(output.toString().trim())
    }
}
