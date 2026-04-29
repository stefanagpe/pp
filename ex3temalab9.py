import json       # pentru serializarea cache-ului in fisier
import time       # pentru timestamp-uri
import urllib.request  # pentru cereri HTTP GET
import urllib.error    # pentru prinderea erorilor de URL
from datetime import datetime  # pentru timestamp-uri lizibile
from pathlib import Path       # pentru gestionarea cailor de fisiere


CACHE_FILE = Path("http_cache.txt")  # fisierul in care se stocheaza cache-ul
CACHE_TTL_SECONDS = 3600             # timpul de expirare al cache-ului: 1 ora

# interfata
class HttpGetInterface:
    def get(self, url: str) -> str:
        raise NotImplementedError  # trebuie implementat de subclase

# efectueaza cererea HTTP reala
class RealHttpGet(HttpGetInterface):
    def get(self, url: str) -> str:
        try:
            with urllib.request.urlopen(url, timeout=10) as response:  # deschide URL-ul cu timeout de 10s
                raw = response.read()                                   # citeste bytes bruti
                try:
                    return raw.decode("utf-8")          # incearca decodarea UTF-8
                except UnicodeDecodeError:
                    return raw.decode("latin-1", errors="replace")  # fallback la latin-1
        except urllib.error.URLError as e:
            return f"[EROARE] Nu s-a putut accesa '{url}': {e.reason}"  # eroare de retea
        except Exception as e:
            return f"[EROARE] {e}"  # orice alta eroare neasteptata

# incarcare/salvare cache
def _load_cache() -> dict:
    if not CACHE_FILE.exists():  # daca fisierul nu exista, returneaza dict gol
        return {}
    try:
        with open(CACHE_FILE, "r", encoding="utf-8") as f:
            return json.load(f)           # parseaza JSON din fisier
    except (json.JSONDecodeError, ValueError):
        return {}                         # returneaza dict gol daca fisierul e corupt


def _save_cache(cache: dict):
    with open(CACHE_FILE, "w", encoding="utf-8") as f:
        json.dump(cache, f, ensure_ascii=False, indent=2)  # scrie cache-ul ca JSON formatat

# inconjoara RealHttpGet cu logica de caching
class CachingProxyHttpGet(HttpGetInterface):
    def __init__(self, real_subject: HttpGetInterface, ttl: int = CACHE_TTL_SECONDS):
        self._real = real_subject  # referinta catre subiectul HTTP real
        self._ttl = ttl            # timp de viata in secunde

    def get(self, url: str) -> str:
        cache = _load_cache()   # incarca cache-ul curent din fisier
        now = time.time()       # timestamp-ul curent Unix

        if url in cache:
            entry = cache[url]                          # obtine intrarea din cache pentru acest URL
            age = now - entry["timestamp"]              # calculeaza varsta in secunde
            ts_human = datetime.fromtimestamp(entry["timestamp"]).strftime("%Y-%m-%d %H:%M:%S")

            if age < self._ttl:                         # cache-ul este inca valid
                print(f"[PROXY] cache HIT - {url}")
                print(f"        stocat la {ts_human}, varsta: {age:.0f}s")
                return entry["response"]                # returneaza raspunsul din cache
            else:                                       # intrarea din cache a expirat
                print(f"[PROXY] cache EXPIRAT - {url}")
                print(f"        varsta: {age:.0f}s > TTL: {self._ttl}s. Se reinnoire...")
        else:
            print(f"[PROXY] cache MISS - {url}")        # URL-ul nu exista in cache
            print(f"        Se face cererea reala...")

        response = self._real.get(url)  # efectueaza cererea HTTP GET reala

        cache[url] = {                  # construieste intrarea in cache
            "url": url,
            "timestamp": now,
            "timestamp_human": datetime.fromtimestamp(now).strftime("%Y-%m-%d %H:%M:%S"),
            "response": response,
        }
        _save_cache(cache)              # salveaza cache-ul actualizat in fisier
        print(f"        Raspuns salvat in cache ({CACHE_FILE})")
        return response

# DEMO
def run_demo():
    real = RealHttpGet()               # creaza subiectul HTTP real
    proxy = CachingProxyHttpGet(real)  # il inconjoara cu proxy-ul de caching

    urls = [
        "https://httpbin.org/get",   # prima cerere  -> cache MISS
        "https://httpbin.org/uuid",  # a doua cerere -> cache MISS
        "https://httpbin.org/get",   # URL repetat   -> cache HIT
    ]

    print("PROXY HTTP GET cu CACHING")

    for url in urls:
        print(f"\nGET {url}")
        response = proxy.get(url)                            # trimite cererea prin proxy
        preview = response[:200].replace("\n", " ").strip()  # afiseaza primele 200 de caractere
        print(f"        Raspuns: {preview}...")

    print(f"\nFisier cache: {CACHE_FILE.resolve()}")

    cache = _load_cache()                   # reincarca cache-ul pentru sumar
    print(f"Intrari in cache: {len(cache)}")
    for url, entry in cache.items():
        print(f"  {url}")
        print(f"    Timestamp: {entry['timestamp_human']}")
        preview = entry['response'][:100].replace('\n', ' ')  # afiseaza un preview scurt al raspunsului
        print(f"    Raspuns:   {preview}...")


if __name__ == "__main__":
    run_demo()
