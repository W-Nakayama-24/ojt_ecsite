package ojt_ecsite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 検索条件のバリデーションや検索実行のメソッドを呼び出し,検索結果を生成するクラス.
 *
 * @author nakayama
 *
 */
public class SearchResultMaker {

    /**
     * ユーザー入力で指定された検索条件がバリデーション条件に違反していないか検証するクラス.
     *
     * @param inputParameterMap ユーザー入力で指定された検索条件を格納したMap
     * @return searchResultJson 検索結果のJSON文字列
     * @throws JsonProcessingException 検索結果をJSON文字列に変換する際にエラーが発生した場合にスローされる例外
     */
    public String makeSearchResult(Map<String, String[]> inputParameterMap) throws JsonProcessingException {
        // 入力チェック
        InputChecker inputChecker = new InputChecker();
        ojt_ecsite.InputChecker.CheckResult checkResult = inputChecker.validateSearchParameters(inputParameterMap);

        ObjectMapper mapper = new ObjectMapper();
        String searchResultJson;
        switch (checkResult) {
            case VALID:
                // 検索条件をオブジェクトにセット
                String productName = null;
                int minPrice = 0;
                int maxPrice = 0;

                // クライアント側より取得した値がnullや空文字列でない場合のみ,初期値へ代入する.
                if (inputParameterMap.get(InputConstant.KEY_PRODUCTNAME) != null
                        && inputParameterMap.get(InputConstant.KEY_PRODUCTNAME)[InputConstant.FIRST_VALUE] != null
                        && !inputParameterMap.get(InputConstant.KEY_PRODUCTNAME)[InputConstant.FIRST_VALUE].isEmpty()) {
                    productName = inputParameterMap.get(InputConstant.KEY_PRODUCTNAME)[InputConstant.FIRST_VALUE];
                }

                if (inputParameterMap.get(InputConstant.KEY_MINPRICE) != null
                        && inputParameterMap.get(InputConstant.KEY_MINPRICE)[InputConstant.FIRST_VALUE] != null
                        && !inputParameterMap.get(InputConstant.KEY_MINPRICE)[InputConstant.FIRST_VALUE].isEmpty()) {
                    minPrice = Integer
                            .parseInt(inputParameterMap.get(InputConstant.KEY_MINPRICE)[InputConstant.FIRST_VALUE]);
                }

                if (inputParameterMap.get(InputConstant.KEY_MAXPRICE) != null
                        && inputParameterMap.get(InputConstant.KEY_MAXPRICE)[InputConstant.FIRST_VALUE] != null
                        && !inputParameterMap.get(InputConstant.KEY_MAXPRICE)[InputConstant.FIRST_VALUE].isEmpty()) {
                    maxPrice = Integer
                            .parseInt(inputParameterMap.get(InputConstant.KEY_MAXPRICE)[InputConstant.FIRST_VALUE]);
                }

                SearchParameter searchParameter = new SearchParameter(productName, minPrice, maxPrice);

                // 検索を実行、結果をList<Product>で取得
                List<Product> foundProductList = new ArrayList<>();
                foundProductList = SearchEngine.searchProductData(searchParameter);
                searchResultJson = mapper.writeValueAsString(foundProductList);
                break;

            case INVALID_ALL_INPUT_EMPTY:
                ErrorMessage messageAllEmpty = new ErrorMessage(ErrorId.ALL_INPUT_EMPTY);
                searchResultJson = mapper.writeValueAsString(messageAllEmpty);
                break;

            case INVALID_CONTAINS_QUOTATION:
                ErrorMessage messageContainsQuotation = new ErrorMessage(ErrorId.CONTAINS_QUOTATION);
                searchResultJson = mapper.writeValueAsString(messageContainsQuotation);
                break;

            case INVALID_EXCEEDS_CHARACTERS:
                ErrorMessage messageExceedsCharacters = new ErrorMessage(ErrorId.EXCEEDS_CHARACTERS);
                searchResultJson = mapper.writeValueAsString(messageExceedsCharacters);
                break;

            case INVALID_NOT_UNSIGNED_INTEGER:
                ErrorMessage messageNotUnsignedInteger = new ErrorMessage(ErrorId.NOT_UNSIGNED_INTEGER);
                searchResultJson = mapper.writeValueAsString(messageNotUnsignedInteger);
                break;

            case INVALID_REVERSED_PRICE_RANGE:
                ErrorMessage messageReversedPriceRange = new ErrorMessage(ErrorId.REVERSED_PRICE_RANGE);
                searchResultJson = mapper.writeValueAsString(messageReversedPriceRange);
                break;

            default:
                ErrorMessage messageUnexpectedState = new ErrorMessage(ErrorId.UNEXPECTED_STATE);
                searchResultJson = mapper.writeValueAsString(messageUnexpectedState);
                break;
        }
        return searchResultJson;

    }

}
