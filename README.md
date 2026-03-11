# P2P Dosya Paylaşım Uygulaması (P2P File Sharing)

Bu proje, yerel ağ üzerindeki cihazların birbirlerini otomatik olarak bulmasını ve paylaşılan klasörlerdeki dosyaları birbirlerine aktarmasını sağlayan Java tabanlı bir Peer-to-Peer (P2P) dosya paylaşım uygulamasıdır.

## 🚀 Özellikler

- **Otomatik Peer Keşfi:** UDP protokolü kullanarak yerel ağdaki diğer aktif kullanıcıları otomatik olarak algılar.
- **Güvenilir Dosya Aktarımı:** Dosya transferi için TCP protokolünü kullanır ve verilerin kayıpsız iletilmesini sağlar.
- **Parçalı Dosya Gönderimi:** Büyük dosyaları 250KB'lık parçalar (chunks) halinde, rastgele sırada ve onay mekanizması (ACK) ile gönderir.
- **Kullanıcı Dostu Arayüz (GUI):** Java Swing kullanılarak geliştirilmiş, kolay kullanımlı bir arayüze sahiptir.
- **Çoklu İş Parçacığı (Multi-threading):** Sunucu ve istemci işlemleri eş zamanlı olarak arka planda çalışır, arayüzü dondurmaz.

## 🛠 Kullanılan Teknolojiler

- **Dil:** Java
- **Arayüz:** Java Swing
- **Ağ Protokolleri:**
    - **UDP (Port 4445):** "Broadcasting" ile ağdaki diğer eşleri (peers) bulmak için.
    - **TCP (Port 10000):** Dosya listesi ve dosya içeriklerini güvenli bir şekilde aktarmak için.

## 📁 Dosya Yapısı

- `Main.java`: Uygulamanın giriş noktası.
- `GUI.java`: Kullanıcı arayüzü ve ana kontrol mantığı.
- `BroadcastingServer.java` & `BroadcastingClient.java`: Peer keşif mekanizması.
- `FileServer.java` & `FileClient.java`: Dosya transferi (sunucu ve istemci tarafı).

## 💻 Nasıl Çalıştırılır?

1.  **Projeyi Derleyin:**
    Terminal üzerinden `20200702056/src/` dizinine gidin ve şu komutu çalıştırın:
    ```bash
    cd 20200702056/src/
    javac Main.java GUI.java
    ```
    *(Not: Eğer Main.java bu dizinde yoksa `javac *.java` komutu ile tüm dosyaları derleyebilirsiniz.)*

2.  **Uygulamayı Başlatın:**
    ```bash
    java GUI
    ```
    *(Not: `20200702056/src/GUI.java` içerisinde kendi `main` metoduna sahiptir.)*

3.  **Kullanım Adımları:**
    - **Root of the P2P shared folder:** Diğer kullanıcılarla paylaşmak istediğiniz klasörü "Set" butonu ile seçin.
    - **Destination folder:** Diğer kullanıcılardan gelen dosyaların kaydedileceği klasörü seçin.
    - **Bağlanma:** Menüden `File -> Connect` yolunu izleyerek ağa dahil olun.
    - **İzleme:** "Found files" ve "Downloading files" panellerinden aktif dosya transferlerini takip edebilirsiniz.

## 👤 Geliştirici

- **İsim:** Yavuz Selim Meletlioğlu
- **Öğrenci No:** 20200702056

---
*Bu proje, CSE471 Network dersi kapsamında bir dönem projesi olarak geliştirilmiştir.*
