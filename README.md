## [ContiPerf](http://databene.org/contiperf/)

[![Maven Central](https://img.shields.io/maven-central/v/com.github.javatlacati/contiperf.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.javatlacati%22%20AND%20a:%22contiperf%22) [![Open Source Helpers](https://www.codetriage.com/javatlacati/contiperf/badges/users.svg)](https://www.codetriage.com/javatlacati/contiperf)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fjavatlacati%2Fcontiperf.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fjavatlacati%2Fcontiperf?ref=badge_shield)

![Total questions on stackoverflow](https://img.shields.io/stackexchange/stackoverflow/t/contiperf.svg?style=plastic&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAABKVBMVEWeo6n0gCT0gCX0gyn0gyr0hCv0hS31hi71hi/1hzD1hzH1iDL1iDP1iTP1izf1jDn1jTv1jjz1kED2kUL2kkP2k0X2lkr2mE32mU/2m1P2nFT3nVb3nlj3n1r3oV33ol73ol/3o2H3pGL3pWT3qGr4rnP4rnT4sHb4snv5tH75tYD5uIX5u4n5u4r5vo/5v5D6wJP6wZX6w5j6x5/6yaL6yqT7zKf7zqv70rH70rL707P71LT71LX71bf71rj817n817r82b382b782r/828D83MP84cv95dL95tT959X96Nf96dn96tv969z969397N797eD97uH+8OT+8OX+8ef+8+r+9e3+9e7+9vD+9/H++PL++fT/+vb/+/j//fv//fz//vz//v3///+EK2URAAABAUlEQVR4AcXTWTdCYRTG8Z5XIiEhAxkUomRW5kEIxQkZHBznPN//Q7C6Yb3vrrO44H+51+9i74sdoE//BErD5abAjqD/uRngvkLSbQTcCslFINcIzAfzpJeC2pXBbRSYfaUdR+hSBLwfAwYsXneg91EEfM8phPd4pDDhGmDr2CN5GIHKOstAVgd2J/rWbLI6CCRq0215HVQSANozFt/SQPfpjbDDeToIqOSJV2iN1eQrHlZ6AMTXz8o0QHHzyiXpbI8AWKUJxoHQaKZw4bA0E30RwFQY9VqG5jaqFAA9a2dhsquOijoIfMG7g6VU7OlzogMtAeBbvwJaPwZm/uDPfvMDVw08L8nounsAAAAASUVORK5CYII=)

### Continuous Performance Testing (Junit)

In order to assure software performance, software needs to be tested accordingly as early as possible - only weaknesses diagnosed early can be assessed quickly and cheaply. ContiPerf enables performance testing already in early development phases and in an easy-to-learn manner: 

A developer writes a performance test in form of a JUnit 4 test case and adds performance test execution settings as well as performance requirements in form of Java annotations. When JUnit is invoked by an IDE, build script or build server, ContiPerf activates, performs the tests and creates an HTML report. The report provides a detailed overview of execution, requirements and measurements, even providing a latency distribution chart.

A large feature set for execution settings and performance requirements is available, e.g. Ramp up, warm up, individual pause timing, concurrent exection of test groups and more.

### Requirements

You need at least Java 5 and JUnit 4.7 to use ContiPerf

### Licence

ContiPerf is Open Source and you can choose among the following licenses:

  * [Apache License 2.0](Apache_License-2.0.txt)
  * [Lesser GNU Public License (LGPL) 3.0](lgpl-version3.txt)
  * [Eclipse Public License 1.0](epl-v10.html)
  * [BSD License](bsd-license.txt)

[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fjavatlacati%2Fcontiperf.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fjavatlacati%2Fcontiperf?ref=badge_large)

### Getting help / getting involved

If you are stuck, found a bug, have ideas for ContiPerf or want to help, visit the [forum](http://databene.org/forum).

### More

Check out [Wiki](https://github.com/lucaspouzac/contiperf/wiki).