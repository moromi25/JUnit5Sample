# 実際に書いたテスト
## テスト対象の機能の仕様
- あるサービスへのアクセス可否をチェックするメソッドのテスト
    - 管理者が設定できる共通設定と、各社員が設定できる個別設定が存在する
    - 管理者は全社一律で利用可否をコントロールできるほか、社員個人に利用有無の設定を任せることができる

## ソース上での設定の持ち方
- 共通設定の設定値は4パターン。以下のようなEnumを用意して定義している
```EnumCommonConfig.java
public enum EnumCommonConfig{
    UNDEFINED(-1),// 共通設定が未設定
    UNUSE(0),// 全社一律で利用しない
    USE(1),// 全社一律で利用する
    DEPEND_ON_EMPLOYEE(2);// 社員に設定を委ねる

    private final int val;

    private EnumCommonConfig(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
```
- 社員個別設定は`0: 利用しない` と `1: 利用する` の2パターン
    - 製品の歴史上、やむを得ず以下のような感じで定数を集めたjavaに定義。。というレガシーコードあるある
```java
    private static final int EMPLOYEE_CONFIG_UNUSE = 0;
    private static final int EMPLOYEE_CONFIG_USE = 1;
```

## テストコードで満たしたい要件
- 共通設定の設定値に応じて利用可否のboolean値を返すメソッド：canManageEachConfigByEmployeeが正しく動作するか
    1. 管理者が設定した共通設定が2の場合に、メソッド：canManageEachConfigByEmployeeがtrueになるか
    1. 管理者が設定した共通設定が0または1の場合に、メソッド：canManageEachConfigByEmployeeがfalseになるか
    1. 管理者が共通設定を未設定の場合に、メソッド：canManageEachConfigByEmployeeがシステムデフォルト共通設定に応じたboolean値を返すか。なお、システムデフォルト共通設定の設定値は製品の利用歴に応じて0または2のいずれかが入る
- ※社員個別設定のテストについては記載省略
- サービスアクセス可否を判断するためのメソッド：がcanUseSercice正しく動作するか
    1. 管理者による共通設定がされていない、かつシステムデフォルト共通設定のデフォルト値が0の場合
    1. 管理者による共通設定がされていない、かつシステムデフォルト共通設定のデフォルト値が3の場合
    1. 管理者による共通設定が1の場合
    1. 管理者による共通設定が2の場合
    1. 管理者による共通設定が3の場合、かつ社員個別設定が存在しない場合
    1. 管理者による共通設定が4の場合、かつ社員個別設定が存在しない場合
    1. 管理者による共通設定が3または4の場合、かつ社員個別設定が0の場合
    1. 管理者による共通設定が3または4の場合、かつ社員個別設定が1の場合

前置き長くなりましたが、いよいよテスト書き始めます！

# テストコードを書く
## 共通設定に関するテスト
この3パターンをテストしていきます。
```
1. 管理者が設定した共通設定が2の場合に、メソッド：canManageEachConfigByEmployeeがtrueになるか
2. 管理者が設定した共通設定が0または1の場合に、メソッド：canManageEachConfigByEmployeeがfalseになるか
3. 管理者が共通設定を未設定の場合に、メソッド：canManageEachConfigByEmployeeがシステムデフォルト共通設定に応じたboolean値を返すか
   なお、システムデフォルト共通設定の設定値は製品の利用歴に応じて0または2のいずれかが入る
```
## サービスアクセス可否テスト

## 実際のソースコード
テストの書き方はいくつかありそうですが、今回私が書いたコードがこちら
```canUseServiceTest.java
class canUseServiceTest extends canUseService {
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("共通設定に関するテスト")
    class CanManageEachConfigByEmployeeTest{

        // システムデフォルト設定を返してくれるクラス
        private SystemDefaultCommonConfigProvider mockedDefaultConfigProvider;

        @BeforeAll
        public void setup() {
            mockedDefaultConfigProvider= spy(new SystemDefaultCommonConfigProvider());
        }

        @Test
        @DisplayName("管理者が設定した共通設定が2の場合に、メソッド：canManageEachConfigByEmployeeがtrueになるか")
        void testWithCommonConfigUseOrOnuse() {
            assertTrue(canManageEachConfigByEmployee(DEPEND_ON_EMPLOYEE, mockedDefaultConfigProvider));
        }

        @ParameterizedTest
        @EnumSource(value = EnumCommonConfig.class, names = { "UNUSE", "USE" })
        @DisplayName("管理者が設定した共通設定が0または1の場合に、メソッド：canManageEachConfigByEmployeeがfalseになるか")
        void testWithCommonConfigUseOrOnuse(EnumCommonConfig commonConfig) {
            assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
        }

        @ParameterizedTest
        @DisplayName("管理者が共通設定を未設定の場合に、メソッド：canManageEachConfigByEmployeeがシステムデフォルト共通設定に応じたboolean値を返すか")
        @CsvSource({
                "UNDEFINED, UNUSE",
                "UNDEFINED, DEPEND_ON_EMPLOYEE",
                "UNDEFINED, USE", // 通常ありえないはずだが念のためテスト
                "UNDEFINED, UNDEFINED" // 通常ありえないはずだが念のためテスト
        })
        void testWithCommonConfigUndefined(EnumCommonConfig commonConfig, EnumCommonConfig systemDefaultCommonConfig) {
            assertTrue(commonConfig== UNDEFINED);

            when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(systemDefaultCommonConfig.getVal());

            switch (systemDefaultCommonConfig) {
            case UNUSE:
            case USE:
                assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
                break;
            case UNDEFINED:
                // システムデフォルトが万一存在しなかった場合、ExceptionではなくsysErrが返ってくるかを確認
                assertDoesNotThrow(() -> canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
                break;
            default:
                break;
            }
        }
    }
}
```
