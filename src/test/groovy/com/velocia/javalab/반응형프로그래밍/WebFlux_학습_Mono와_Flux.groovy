package com.velocia.javalab.반응형프로그래밍

import com.velocia.javalab.Color
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import java.util.stream.IntStream
import java.util.stream.LongStream

class WebFlux_학습_Mono와_Flux extends Specification{
    private final Random 난수 = new Random()
    /*
     mono 안에 값이 없으면 null 을 반환한다.

     mono는 여러번 block 이 가능하다. 즉 mono는 여러번 재사용이 가능하다.
     -> 이에 대한 정확한 문서를 찾아 보던 중, 다음과 같은 문구를 발견하였다
     -> Project Reactor streams are also reusable, which is a significant difference from Java 8 Streams.
     ->즉, Stream 과 달리 mono는 여러번 재사용이 가능하다.

     MonoJust 를 뜯어보니까 불변객체인듯하다 (value 가 final 객체)
     ->다만, deepcopy 를 하는지는 모르니 완전히 불변객체라 하긴 힘들지도

     여기서 생긴 궁금증, mono.filter/map 같은 함수를 사용할 경우 새로운 mono 객체를 생성하는걸까?
     -> A001-1 테스트 참조
     */
    def "A001 스트림에서 작동하던 함수들을 Mono에서 동작시켜보기"() {
        given:
        Long mono_안에_들어갈_데이터 = 난수.nextLong()
        Mono<Long> mono = Mono<Long>.just(mono_안에_들어갈_데이터 as Long)
        Long origin = mono.block()
        Long filter = mono.filter(x -> x > 200).block()
        Long filter2 = mono.filter(x -> x <= 200).block()
        Long map = mono.map(x -> x + 1).block()
        boolean mono가_200보다_작은가 = mono_안에_들어갈_데이터 > 200

        expect:
        mono_안에_들어갈_데이터 == mono.block()
        origin == mono_안에_들어갈_데이터
        filter == (mono가_200보다_작은가 ? mono_안에_들어갈_데이터 : null)
        filter2 == (mono가_200보다_작은가 ? null : mono_안에_들어갈_데이터)
        map == mono_안에_들어갈_데이터 + 1

        cleanup:
        "테스트 데이터 출력"('A001', mono.block(), origin, filter, filter2, map)
    }

    /*
    mono에 filter map 같은 함수를 사용하면 새로운 mono객체를 만드는 듯 하다.
    -> - filter 사용시 MonoFilterFuseable 형식으로
    -> - map 사용시 MonoMapFuseable 형식으로 말이다.
    -> - 또한 just 로 Mono 를 생성하면 MonoJust 가 생성된다
    -> 세 자료형 모두 Mono 를 상속받았다.
    
    filter map 같은 메서드에서도 새로운 객체를 내놓는것을 보니 Mono 는 불변객체인 듯 하다.
    
    그렇다면 Flux 는 어떨까?
    -> B001 계열 테스트 참조
     */
    def "A001-1 mono의 filter map 같은 함수를 사용할 경우 새로운 mono 객체를 생성하는걸까"() {
        given:
        Long mono_안에_들어갈_데이터 = 난수.nextLong()
        Mono<Long> mono = Mono<Long>.just(mono_안에_들어갈_데이터 as long)
        Mono<Long> mono2 = mono.filter(x -> true)
        Mono<Long> mono3 = mono.map(x -> x)

        expect:
        mono != mono2
        mono != mono3

        cleanup:
        "테스트 데이터 출력"('A001-1', mono, mono2, mono3)
    }

    def "B001 mono에서 진행했던 A001 테스트를 Flux 에서도 해보자"() {
        given:
        Long[] 데이터 = LongStream
                .range(0, 난수.nextInt(5)+1)
                .map(x -> 난수.nextLong())
                .toArray()
        Long[] Flux_안에_들어갈_데이터 = new Long[데이터.length + 2]
        for(int i = 0; i < 데이터.length; i++) Flux_안에_들어갈_데이터[i] = 데이터[i]
        Flux_안에_들어갈_데이터[데이터.length] = 200
        Flux_안에_들어갈_데이터[데이터.length + 1] = 201

        Flux<Long> flux = Flux<Long>.just(Flux_안에_들어갈_데이터 as Long[])
        Long[] origin = "Flux 를 Array 로"(flux)
        Long[] filter = "Flux 를 Array 로"(flux.filter(x -> x > 200))
        Long[] filter2 = "Flux 를 Array 로"(flux.filter(x -> x <= 200))
        Long[] map = "Flux 를 Array 로"(flux.map(x -> x + 1))


        expect:
        Long[] filter예상값 = Arrays.stream(Flux_안에_들어갈_데이터).mapToLong(x -> x).filter(x -> x > 200).toArray()
        Long[] filter2예상값 = Arrays.stream(Flux_안에_들어갈_데이터).mapToLong(x -> x).filter(x -> x <= 200).toArray()
        Long[] map예상값 = Arrays.stream(Flux_안에_들어갈_데이터).mapToLong(x -> x).map(x -> x + 1).toArray()
        origin == Flux_안에_들어갈_데이터
        filter == filter예상값
        filter2 == filter2예상값
        map == map예상값

        cleanup:
        String red = Color.RED
        String green = Color.GREEN
        StringBuilder output = new StringBuilder("\n\nB001 - 테스트 데이터를 출력합니다!\n").append("원본 데이터 : ")
        for(Long el : origin) output.append(el > 200 ? green : red).append(el).append(Color.RESET).append(" | ")
        output.append("\nx > 200 필터 | ")
        for(Long el : filter) output.append(green).append(el).append(Color.RESET).append(" | ")
        output.append("\nx <= 200 필터 | ")
        for(Long el : filter2) output.append(red).append(el).append(Color.RESET).append(" | ")
        output.append("\nx + 1 맵 | ")
        for(Long el : map) output.append(Color.YELLOW).append(el).append(Color.RESET).append(" | ")
        print(output.toString() + "\n\n")
    }

    static <T> T[]  "Flux 를 Array 로"(Flux<T> flux) {
        return flux.collectList().block().toArray(Object as T[])
    }

    static def "테스트 데이터 출력"(String testTitle, Object... data) {
        StringBuilder output = new StringBuilder('| ')
        for(Object datum : data) output.append(datum).append(' | ')
        print('\n\n' + testTitle + ' : 테스트 데이터를 출력합니다!\n')
        print(output.toString().trim() + "\n\n")
    }
}
