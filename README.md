# 💀 Hardcore Revival Plugin

> **Türkçe** | [English](README_EN.md)

Minecraft hardcore sunucuları için dinamik canlandırma sistemi ve otomatik dünya sıfırlama özelliklerine sahip bir plugin.

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/Lisans-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)

## ✨ Özellikler

### 🎮 Canlandırma Sistemi
- **3 Canlanma Hakkı** - Her oyuncu 3 kez canlandırılabilir
- **Spectator Modu** - Ölen oyuncular canlandırılana kadar izleyici olarak kalır
- **Komut Tabanlı** - `/revive <oyuncu>` komutu ile takım arkadaşını canlandır

### 💎 Dinamik Maliyet Sistemi
Canlandırma maliyeti oyun ilerledikçe değişir:

| Aşama | Maliyet | Tetikleyici |
|-------|---------|-------------|
| **Erken Oyun** | 20 Demir | Varsayılan başlangıç maliyeti |
| **Orta Oyun** | 5 Elmas | Birisi "Elmaslar!" başarımını aldığında |
| **Geç Oyun** | 5+ Elmas | Her 2 gecede +1 elmas artar |

### 🌍 Dünya Sıfırlama
- **Otomatik Sıfırlama** - Her iki oyuncu da kalıcı öldüğünde (0 canlanma hakkı)
- **Çift Ölüm Sıfırlama** - İki oyuncu aynı anda öldüğünde
- **Tam Temizlik** - Overworld, Nether ve End boyutlarını siler
- **Yeni Başlangıç** - Sunucu kapanır, manuel restart gerektirir

### 🌐 Çok Dilli Destek
- Varsayılan diller: **Türkçe** ve **İngilizce**
- Config dosyasından kolayca yeni diller eklenebilir
- Tüm oyun mesajları özelleştirilebilir

### 📊 İlerleme Takibi
- Elmas başarımından sonra geçen geceleri sayar
- Oyuncu canlanma haklarını sunucu yeniden başlatılsa bile saklar
- Kalıcı konfigürasyon sistemi

## 📦 Kurulum

### Gereksinimler
- **Minecraft Sunucu**: 1.20.1 (Paper/Spigot/Bukkit)
- **Java**: 17 veya üzeri
- **Maven**: Kaynak koddan derlemek için

### Hızlı Kurulum

1. **İndir veya Derle**
   ```bash
   git clone https://github.com/pxloch/pxlochs-revival.git
   cd pxlochs-revival
   mvn clean package
   ```

2. **Plugin'i Yükle**
   - `target/PxlochsRevival-1.0-SNAPSHOT.jar` dosyasını sunucunuzun `plugins/` klasörüne kopyalayın
   - Sunucuyu yeniden başlatın veya reload yapın

3. **Yapılandır** (İsteğe Bağlı)
   - Plugin otomatik olarak `plugins/PxlochsRevival/config.yml` oluşturur
   - Dil ayarını ve mesajları istediğiniz gibi düzenleyebilirsiniz

## 🎯 Kullanım

### Komutlar

| Komut | Açıklama | İzin |
|-------|----------|------|
| `/revive <oyuncu>` | Ölen bir oyuncuyu canlandır | Varsayılan |
| `/lives` | Canlanma haklarını ve maliyetleri göster | Varsayılan |

**Alternatif Komutlar:**
- `/revive` → `/rev`, `/resurrect`
- `/lives` → `/liv`

### Oyun Akışı

```
Oyuncu Ölür
    ↓
Canlanma Hakkı Var mı?
    ↓ Evet                    ↓ Hayır
Spectator Olur          Kalıcı Ölüm
    ↓                          ↓
Takım Arkadaşı         Dünya Sıfırlama
/revive Kullanır       10 Saniye Geri Sayım
    ↓                          ↓
Yeterli Malzeme?       Sunucu Kapanır
    ↓ Evet                    
Canlandırıldı!         
-1 Canlanma Hakkı
```

### Örnek Senaryolar

#### Senaryo 1: Erken Oyun Canlandırma
```
Oyuncu1 ölür → Oyuncu2 /revive Oyuncu1 kullanır
Maliyet: 20 Demir
Oyuncu1 canlanır, 2/3 hak kalır
```

#### Senaryo 2: Elmas Başarımı
```
Oyuncu2 "Elmaslar!" başarımını alır
→ Canlandırma maliyeti değişir: 20 Demir → 5 Elmas
→ Gece sayacı başlar
→ Tüm oyunculara duyuru yapılır
```

#### Senaryo 3: İlerleyen Zorluk
```
Gece 1-2: 5 Elmas
Gece 3-4: 6 Elmas (duyuru: "2 Gece Geçti! Maliyet arttı")
Gece 5-6: 7 Elmas
Ve böyle devam eder...
```

#### Senaryo 4: Kalıcı Ölüm
```
Oyuncu1 ölür (0 hak kaldı)
→ Kalıcı spectator olur
→ 10 saniye geri sayım başlar
→ Dünyalar silinir (/world, /world_nether, /world_the_end)
→ Sunucu kapanır
→ Admin sunucuyu yeniden başlatır
```

## ⚙️ Yapılandırma

Plugin otomatik olarak `plugins/PxlochsRevival/config.yml` dosyasını oluşturur.

### Dil Değiştirme

```yaml
settings:
  language: tr  # 'en' olarak değiştirerek İngilizce'ye geçebilirsiniz
```

### Yeni Dil Ekleme

1. `config.yml` dosyasını açın
2. `messages:` bölümüne yeni bir dil ekleyin:

```yaml
messages:
  tr:
    PLAYER_DIED: "&e{player} &cöldü!"
    # ... diğer mesajlar
  
  en:
    PLAYER_DIED: "&e{player} &cdied!"
    # ... diğer mesajlar
  
  de:  # Yeni dil (Almanca örneği)
    PLAYER_DIED: "&e{player} &cist gestorben!"
    PERMANENT_DEATH: "&c&l{player} IST PERMANENT GESTORBEN!"
    # ... tüm mesajları çevirin
```

3. `settings.language` değerini yeni dil kodunuza değiştirin:
```yaml
settings:
  language: de
```

### Mesaj Özelleştirme

Renk kodları:
- `&0-&9, &a-&f` = Renkler
- `&l` = Kalın
- `&n` = Altı çizili
- `&o` = İtalik

Yer tutucular:
- `{player}` = Oyuncu adı
- `{cost}` = Maliyet
- `{lives}` = Kalan can
- `{seconds}` = Saniye

### Yapılandırma Sıfırlama
Plugin durumunu sıfırlamak için:
1. Sunucuyu durdurun
2. `plugins/PxlochsRevival/config.yml` dosyasını silin
3. Sunucuyu başlatın (varsayılan ayarlar ile yeniden oluşturulur)

## 🔧 Kaynak Koddan Derleme

### Ön Gereksinimler
- Java 17 JDK
- Maven 3.6+

### Derleme Adımları
```bash
# Depoyu klonla
git clone https://github.com/pxloch/pxlochs-revival.git
cd pxlochs-revival

# Plugin'i derle
mvn clean package

# Çıktı konumu
target/PxlochsRevival-1.0-SNAPSHOT.jar
```

### Geliştirme Ortamı
```bash
# Bağımlılıkları yükle
mvn clean install

# Testleri çalıştır (varsa)
mvn test

# JavaDocs oluştur
mvn javadoc:javadoc
```

## 📝 Proje Yapısı

```
pxlochs-revival/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/pxloch/pxlochsrevival/
│       │       └── PxlochsRevival.java
│       └── resources/
│           ├── plugin.yml
│           └── config.yml
├── pom.xml
├── README.md
├── README_EN.md
└── LICENSE
```

## 🐛 Bilinen Sorunlar ve Sınırlamalar

- **Pterodactyl Uyumluluğu**: Bazı hosting panellerinde dosya izinleri nedeniyle dünya silme işlemi başarısız olabilir. Plugin sunucuyu kapatır ve dünyalar dosya yöneticisinden manuel silinmelidir.
- **Gece Sayacı**: Gece takibi için en az bir oyuncunun çevrimiçi olması gerekir.
- **Başarım Algılama**: Sadece vanilla Minecraft "Elmaslar!" başarımını algılar.

## 🤝 Katkıda Bulunma

Katkılarınızı bekliyoruz! Pull Request göndermekten çekinmeyin.

### Nasıl Katkıda Bulunulur
1. Depoyu fork edin
2. Özellik dalınızı oluşturun (`git checkout -b ozellik/HarikaOzellik`)
3. Değişikliklerinizi commit edin (`git commit -m 'Harika özellik eklendi'`)
4. Dalınıza push yapın (`git push origin ozellik/HarikaOzellik`)
5. Pull Request açın

### Katkı Fikirleri
- [ ] Özelleştirilebilir maksimum canlanma hakkı sayısı
- [ ] 2'den fazla oyuncu desteği
- [ ] Özel başarım takibi
- [ ] Totem of Undying entegrasyonu
- [ ] Canlandırma ses efektleri
- [ ] Partikül efektleri
- [ ] Büyük sunucular için veritabanı desteği
- [ ] Daha fazla dil desteği
- [ ] Discord webhook bildirimleri

## 📄 Lisans

Bu proje MIT Lisansı altında lisanslanmıştır - detaylar için [LICENSE](LICENSE) dosyasına bakın.

## 👥 Yazarlar

- **Pxloch** - *İlk geliştirme* - [GitHub](https://github.com/pxloch)

## 🙏 Teşekkürler

- Hardcore Minecraft oynanışından ilham alınmıştır
- Paper/Spigot 1.20.1 için geliştirilmiştir

## 📞 Destek

- **Sorunlar**: [GitHub Issues](https://github.com/pxloch/pxlochs-revival/issues)
- **Tartışmalar**: [GitHub Discussions](https://github.com/pxloch/pxlochs-revival/discussions)

## 📊 İstatistikler

![GitHub yıldızları](https://img.shields.io/github/stars/pxloch/pxlochs-revival?style=social)
![GitHub forkları](https://img.shields.io/github/forks/pxloch/pxlochs-revival?style=social)
![GitHub sorunları](https://img.shields.io/github/issues/pxloch/pxlochs-revival)
![GitHub pull requestler](https://img.shields.io/github/issues-pr/pxloch/pxlochs-revival)

---

Minecraft topluluğu için ❤️ ile yapıldı