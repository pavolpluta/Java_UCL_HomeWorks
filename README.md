# Java_UCL_HomeWorks

### DU1
  - Working on project simulation
### DU2 - Meeting Centres
  - Interactive console-based application which allows you to work with Meeting Centres and Meeting rooms (create, edit, delete...). It also allows you to import data from .csv file (ImportData.csv in this case) and save them to another one (SaveData.csv - file location is hardcoded inside the code).
  - Aplikace umožní spravovat Meeting Centres a Meeting Rooms. Aplikace zobrazí seznam možností, které lze aktuálně provádět. Zde bude možnost zobrazení všech Meeting Centres, přičemž v seznamu bude pro každé Meeting Centre zobrazen jeho název a kód. Další možností hlavního menu bude přidání nového Meeting Centra a následně import dat ze souboru, který bude popsán níže. Budou zde ještě dvě možnosti, kterými se aplikace ukončí. Jedna z nich bude navíc obsahovat uložení dat do souboru tak, aby si aplikace provedené změny při příštím otevření pamatovala.
Ve chvíli, kdy uživatel vybere možnost zobrazení všech Meeting Center, aplikace vypíše na obrazovku seznam a pod tento seznam vypíše opět menu možností, kam se uživatel může ve svém počínání ubírat. Toto bude obecné pravidlo pro jakýkoliv krok v aplikaci, že pod příslušným zobrazením bude opět možnost volby, i kdyby tam měla být třeba jen jedna volba a to zpět na předchozí krok.
Po vylistování všech center by tu měla být možnost zobrazení detailu konkrétního meeting centra, kde budou k němu zobrazeny všechny informace a zároveň možnosti pro editaci, zobrazení seznamu Meeting Rooms nebo přidání nové místnosti.
Zároveň má uživatel možnost založit nové Meeting Centre, kde máte možnost zobrazit všechny potřebné atributy a následně se ptát na každý z nich, samozřejmě zde bude i možnost přidat nové místnosti.
Další možností bude smazat Meeting Centre. Ve chvíli, kdy uživatel tuto funkčnost spustí, tak bude zobrazen dotaz, ve kterém uživatel potvrdí nebo stornuje, že chce opravdu Meeting Centre smazat. Pokud bude Meeting Centre smazáno, pak budou smazány i do něj patřící Meeting Rooms.
S Meeting Rooms lze provádět stejné operace jako s Meeting Centres, tedy je vybrat, čímž se zobrazí detailní informace, upravit a smazat stejným způsobem jako Meeting Centres.
