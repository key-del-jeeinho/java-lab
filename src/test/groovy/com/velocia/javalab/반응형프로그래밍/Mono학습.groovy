package com.velocia.javalab.반응형프로그래밍

import reactor.core.publisher.Mono
import spock.lang.Specification

class Mono학습 extends Specification{
    def "모노 만들어보기"() {
        //모노는 여러번 block 이 가능하다.
        //그 이유는 모노 자체가 변하지 않아서 인듯하다 (불변객체인지 테스트해보자).
        //모노.map 을 할 경우 새로운 Mono 를 생성해서 반환하는 것 같다.
        //모노 안에 값이 없으면 null 을 반환한다.
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
