# 抽題APP
* 20210809
    * 想了很久不知道要做甚麼，後來決定在確定老媽要的app長甚麼樣子前，先做個比較簡單的東西，順便更熟悉android studio。
    * 後來想到的idea是把之前期末考前無聊做的抽題系統變成app
    * 原本的系統：
        * 所有東西都內建在程式碼裡
        * 沒有介面，只有很醜的cmd視窗
        * ![](https://i.imgur.com/Ozy6E97.png)
    * 希望新系統要有的東西：
        * 好看ㄉ介面
        * 錯題記錄
        * 錯題區可以重抽錯題再寫一次
        * 一個app就可以搞定所有科目
        * 可以直接在介面中新增科目和題目
    * 介面草圖：
        ![](https://i.imgur.com/7CV3z9a.png)
    * 目前進度：
        * 建完每個fragment的xml檔
        * nav_graph連接完 
        * 可以從home的按鈕跳到各個介面了，不過各介面的按鈕還沒有功能
* 20210810
    * 難以決定sql database裡要每題存一筆還是每個科目存一筆
        * 原本是一題存一筆，可是覺得好浪費空間
        * 因為其實只需要知道每個科目裡哪些編號是抽過的哪些沒抽過
        * 決定一個章節存一筆，用list存未寫的題號(或是寫過的題號也可以)還有錯的題號
          ![](https://i.imgur.com/SuSSmYZ.png)
        * 後來發現一個大問題，sql裡不能存List
        * 又想改回一題存一筆
        * 可是後來又發現這樣沒辦法用listAdapter呈現在updateFragment裡
        * 最後只好建兩個表分別存Ex(一題)和Subject(一科)
        * 兩個表寫在不同dataset裡，分別有自己的Dao和RoomDatabase
        * 可是到修改application的時候就卡住了，因為一個app好想只能設一個application
        * 後來查到要怎麼在database裡建兩個表->兩個表都寫@Entity，在輸入Entity的地方都要兩個都打
          ```kotlin
          @Database(entities = [Ex::class,Subject::class], version = 2, exportSchema = false)
            abstract class ExRoomDatabase : RoomDatabase() {
           ```
        * application要記得修改AndroidManifest.xml 
          ```xml
              <application
                android:name="com.example.randomexercise.RandomExerciseApplication"
          ```
    *  目前進度：
        *  建好資料庫(dataEntity+Dao+RoomDatabase+Application)
        *  可以新增科目了
        *  科目可以顯示在updateFragment上了
* 20210811
    * coroutine error：
        * 開始寫抽題的function
        * 原本打算把抽好的結果return回來
        * 可是報error說用到ExDao就要放在coroutine裡，不能用main thread跑:java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
        * 可是放到viewModelScope.launch裡還是一樣
        * 後來發現可以用viewModelScope.launch (Dispatchers.Default)來指定用coroutine跑
        * 但在coroutine裡面不能return
        * 後來查到必須用livedata來存，然後在fragment中用observe來獲取值
        * 用livedata.value=...會報錯
        * 後來查到用postValue(...)就可以了
    * 抽題介面
        * 題號的地方改成用兩個TextView，分別放chapter和number
        * 用observe的方式更新
          ```kotlin
            viewModel.pickedChapter.observe(this.viewLifecycleOwner){
            binding.chapter.text=it.toString()
            chapter=it
            }
            viewModel.pickedNumber.observe(this.viewLifecycleOwner){
            binding.number.text=it.toString()
            number=it
            }
          ```
    * 目前進度：
        * 可以抽題了喔耶
        * 抽完題後不知道為甚麼沒有更新到remain和wrong
* 20210812 
    * 更新remain和wrong：
        * 原本給的指令是
            ```sql
            UPDATE Subject SET remain=:remain and wrong=:wrong WHERE subject=:subject
            ```
        * 可是一直沒辦法更新
        * 連直接從database inspector裡打指令也沒辦法
        * 後來發現好像是因為沒辦法同時更新兩個資料
        * 最後決定用像Inventory app的方式，先create一個新的ExEntry，再用@Update整筆更新 
    * 不抽重複題
        * 重複的題目不要再抽一次
        * 一開始的想法是寫一個function，在抽完題之後檢查，如果這題抽過了(correct>=0)，就再次呼叫drawLot()
        * 如果在drawLot()裡再呼叫drawLot()會報錯"java.lang.StackOverflowError stack size 8MB"，因為這樣會過度遞迴
        * 只好改成還是存一個liveData(doneFlag)
        * 後來想到一個比較好的方法，直接在ExDao裡把指令改成...WHERE correct=-1
        * 錯題和新題分兩個指令寫，錯題的話就是correct=0
    * 希望已經沒有題目的科目不要出現在spinner上
        * 錯題和新題分兩個Dao指令：getWrongSubjectName和getSubjectName
            ```kotlin
            @Query("SELECT Distinct subject FROM Subject WHERE remain>0 ORDER BY subject ASC")
            fun getSubjectName():Flow<List<String>>

            @Query("SELECT Distinct subject FROM Subject WHERE wrong>0 ORDER BY subject ASC")
            fun getWrongSubjectName():Flow<List<String>>
            ```
    * 連續作答到沒有題目
        * 如果是在exerciseFragment連續抽才沒有題目，會沒辦法限制使用者不繼續抽造成程式當掉
        * 在抽新題偵測到沒有remain或是在抽錯題偵測到沒有wrong時就跳出alertDialog
        * 按按鈕回到主選單
        * 在viewModel中用noFlag檢查還有沒有題目
            ```kotlin
            if(mode==0&&ExDao.getRemainCount(subject)==0)
            {
                noFlag.postValue(true)
            }
            else if(mode==1&&ExDao.getWrongCount(subject)==0)
            {
                noFlag.postValue(true)
            }
            ```
        * 不知道為甚麼寫==0的話要多按一下才有反應，然後remain就變成-1了
        * 後來改成==1，新題正常了，可是wrong會在剩一題的時候就不給抽，超奇怪，待處理
    * 善用database inspector
        * 測試的時候常常會需要把資料庫還原到甚麼都沒按的狀態
        * 利用database inspector給指令
            ```sql
            UPDATE Ex SET correct=-1
            UPDATE Subject SET remain=9 WHERE subject="普物"
            UPDATE Subject SET wrong=0 WHERE subject="普物"
            ```
    * 目前進度
        * 可以選擇抽新題或抽錯題
        * 不會抽到重複題
        * 沒有題目會跳出alertDialog 
* 20210813
    * 在只有一題錯題的時候會沒辦法更新
    * 只按一下O就會跳出alertDialog，然後錯題會一直在
      ![](https://i.imgur.com/qIxYrlo.png)
      ![](https://i.imgur.com/rGFXkOE.png)
    * Room cannot verify the data integrity. Looks like you’ve changed schema but forgot to update the version number. You can simply fix this by increasing the version number.
      在table中新增了新的欄位，所以entity被改變了
      要去ExRoomDatabase中修改version->version++
    * delete完之後要記得回上一頁(                findNavController().navigateUp())，不然會報錯
      ![](https://i.imgur.com/rvkMHvG.png)

    * App icon：
        * 因為怕直接用網路上的圖會有版權問題，乾脆自己畫
        * 用goodnote畫再用小畫家塗色
        * ![](https://i.imgur.com/nNBRG0U.png)
        * 結果發現用小畫家背景會有顏色，好醜
        * 找不到適當的方法移除背景，因為畫出來的東西超級鋸齒
        * 後來決定載一個平板繪圖軟體來試試看：MediBang Paint
        * 介面高級到令人不知所只好改版
        * ![](https://i.imgur.com/xvWa2GE.png)
        * 換上去好可愛！！！
        * ![](https://i.imgur.com/jM1ttw8.jpg)
    * remain和wrong的計數問題
        * 後來決定在getRemainCount或getWrongCount等於0的時候才noFlag.postValue(true)
        * 只是這樣remain和wrong最後的值會停在-1
        * 另外寫一個setToZero來把remain或wrong設成0
        * ![](https://i.imgur.com/ZhOrGJn.png)
    * 配色
        * 用 https://material.io/resources/color/#!/?view.left=0&view.right=0&primary.color=6002ee 配了顏色
        * 我真的有色彩障礙欸QQ
    * 目前進度：
        * 新增了detailFragment
        * 可以restart(讓作答完的科目可以重新作答)
        * 也可以delete
        * 換完icon
        * 換完顏色
        * 好像都搞定了欸好讚，明天再來放完整圖
* 20210814
    * 整理了一下code，把不必要的東東刪掉
    * 可能是因為資料庫更新速度的關係，有時候會沒偵測到remain或wrong==0的瞬間，就會一直是負的而沒跳出提醒
        * 改成<=0就noFlag.postValue(true)
    * 完整介面：
        * ![](https://i.imgur.com/q4WAqwP.png)
        * ![](https://i.imgur.com/RrhSl8F.png)
        * ![](https://i.imgur.com/WMeGCFT.png)
        * ![](https://i.imgur.com/vU6TPkb.png)
        * ![](https://i.imgur.com/LnyvhBn.png)
        * ![](https://i.imgur.com/GtPkHHZ.png)
        * ![](https://i.imgur.com/IRWycNu.png)
        * ![](https://i.imgur.com/sejl6r6.png)
        * ![](https://i.imgur.com/nf97Epw.png)
    * 影片在github上有














