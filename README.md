## Implementacja algorytmu do detekcji kroków w systemie Android

Implementacja powyższego algorytmu nosi za sobą wiele wad, które, jeśli chcąc zaimplementować go w faktycznej, komerycjnej aplikacji, mogą znacznie obniżyć poziom aplikacji przy koszcie straty czasu i niepełnie działącej usłudze. Problemy jej implementacji, biorąc pod uwagę łatwodostępne rozwiązania, nie wydaje się słusznym pomysłem.

System Android od *API 19* (od 2013 roku) posiada zaimplementowane metody pozwalające na:
* Liczenie kroków,
* Wywołanie metody przy wykonaniu kroku,

Metody zostały później ulepszone o:
* Pobieranie kumulatywne ilości kroków w danym czasie,
* Subskrypcję detekcji kroków,
* [Wiele więcej](https://developers.google.com/fit/android/)

Implementacja algorytmu, który będzie dokładnie dokonywał rejestracji kroku, jego rodzaju, szybkości biegu lub chodzenia stanowi ciężkie wyzwanie, dlatego nie dziwnym jest, że temat ten stanowi główny wątek wielu [prac naukowych](https://scholar.google.pl/scholar?hl=en&as_sdt=0%2C5&q=step+detection+algorithm&btnG=&oq=step+detection+a). 

## Problem implementacji algorytmu na nowych smartfonach

Podczas tworzenia tej aplikacji napotkałem problem, który definitywnie skreślił możliwość stworzenia **uniwersalnego** kodu pozwalającego na dokładną detekcję kroków. Urządzenie na którym testowałem aplikację (LG V30) okazał się posiadać bardzo dokładne sensory, które w swojej nadgorliwości potrafią oddawać tak dużą ilość danych, że procesor nie jest w stanie nadążyć nad zmianą informacji na ekranie. Co ciekawe, problem ten nie pojawia się u większości starszych, słabszych podzespołowo urządzeń. Telefon zdawał się ignorować metodę `onAccuracyChanged` która była poprawnie wywoływana przy zmianie dokładności, lecz urządzenie nie pozwalało na obniżenie dokładności na tę, która była ustanowiona na początku działania aplikacji `SENSOR_DELAY_NORMAL`. Nawet ustawienie dokładnej ilości mikrosekund co które aplikacja teoretycznie ma otrzymać dane nie pomagało w żadnym stopniu (`SensorManager.SENSOR_DELAY_NORMAL` w metodzie `registerListener` można zamienić na czas w mikrosekundach). 

Zmiana dokładności nastąpywała wraz z:
* Zablokowaniem telefonu (lecz nie zawsze),
* Szybkim ruszaniem telefonu po np. jednej osi (a co za tym idzie, problem pojawi się podczas biegania).

Zmiana dokładności po blokowaniu telefonu może mieć związek z funkcją `Pick up to wake`, a zwiększenie dokładności przy gwałtownych ruchach telefonu wydaje się logicznym rozwiązaniem, gdyż telefon może dokładnie reagować na zmiany nawet jeżeli jest przykładowo w ciągłej wibracji w tramwaju, pociągu. 

Jeśli dobrze przeanalizowałem sytuację, to wnioskuję, że producent sam zaimplementował taką reakcję i deweloper nie jest w stanie tego w żaden sposób zmienić. Może jedynie się dostosować. Finalnie porzuciłem próbę tworzenia algorytmu, ponieważ zdałem sobie sprawę, że nie ma to prawie żadnego celu poza proof-of-concept. W moim przypadku kompleksowość takiego algorytmu wykraczałaby pewne granice logiki. 

Każda aplikacja, która musiała by z bliżej nieznanych przyczyn indywidualnie implementować algorytm detekcji kroków dokładałaby pewnego rodzaju cegiełkę do pogorszenia doświadczeń użytkownika z nimi, gdyż każdy z nich różniłby się od siebie i pewnie żaden z nich nie byłby w pełni dokładny. 

API pozwalające na otrzymanie gotowych informacji na temat przemieszczania się użytkownika ujednolica aplikacje jedocześnie znacznie ułatwiając życie deweloperom, a co za tym idzie użytkownikom. Znacznie lepszym pomysłem jest stworzenie jednego, natywnego dla wszystkich urządzeń kodu, który będzie działał poprawnie na każdym z nich niż tworzenia "koła na nowo". 

## Co należałoby wziąć pod uwagę?

Chcąc stworzyć taki algorytm musielibyśmy wziąć pod uwagę następujące rzeczy:
* Gdzie telefon się znajduje? Czy jest w kieszeni, a jeśli tak to w jakiej pozycji? Dla każdej pozycji należałoby stworzyć osobną ścieżkę logiczną co samo w sobie może zająć ogromną ilość czasu.
* Czy użytkownik trzyma telefon w ręku? Co jeśli włoży go spowrotem do spodni albo do torby? Czy trzymając telefon w ręku użytkownik idzie, stoi czy biegnie? Co jeśli znajduje się na opasce na ramieniu?
* W jakiej orientacji znajduje się urządzenie? 

Algorytm zaimplementowany przez Google jest tak dobry w detekcji co aktualnie dzieje się z urządzeniem, że w wersji [5.0 Android'a](https://www.androidcentral.com/body-detection-explained) zaimplementowało nawet Smart Lock, który pozostawiwa telefon odblokowany, jeśli dojdzie do wniosku, że urządzenie jest z użytkownikiem. Pozwala on na sprawdzenie, czy telefon jest w ręku, kieszeni, na nodze, czy został odłożony np. na stół. 

Kolejnym faktorem, który należy wziąć pod uwagę jest **ilość zasobów** jakich taki algorytm musiałby potrzebować aby stale działać w tle i na bieżąco obliczać kroki. Może to stanowic ogromny problem dla urządzeń z niskimi podzespołami, gdyż oddziałowywałby na procesor oraz na baterię. Google ulepszając swój algorytm pamiętało o tym, dzięki czemu wiemy, że ich rozwiązanie stanowi najmniejsze możliwe obciązenie na urządzenie jednocześnie kolejny raz dodając [deweloperom nowe możliwości interakcji z tymi danymi](https://developers.google.com/android/reference/com/google/android/gms/fitness/RecordingApi). 

Podsumowując, biorąc pod uwagę obecne możliwości chęć stworzenia własnego algorytmu detekcji kroków do aplikacji jest bardzo nieuzasadniona oraz obniża produktywność, gdyż w czasie go tworzenia możemy skupić się na o wiele ważniejszych rzeczach, w szczególności gdy rozwiązanie jest na wyciągnięcie ręki. Implementacja prowadzi do wprowadzenia ogromnej ilości kodu obciązając znacznie zasoby urządzeń na których będzie on działać. Nawet jeśli stworzymy taki algorytm nigdy nie będziemy pewni, że działa on poprawnie na wszystkich urządzeniach (patrz przykład z LG V30), wręcz możemy założyć, że będzie on ograniczony do pewnej puli urządzeń. 
