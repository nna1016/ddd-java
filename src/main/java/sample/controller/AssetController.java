package sample.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import sample.ActionStatusType;
import sample.context.Dto;
import sample.context.actor.ActorSession;
import sample.model.asset.CashInOut;
import sample.model.asset.CashInOut.RegCashOut;
import sample.model.constraints.AbsAmount;
import sample.model.constraints.Currency;
import sample.usecase.AssetService;

/**
 * API controller of the asset domain.
 */


@RestController
/**
 * @RequestMappingを書くことで一括でルーティングができる
 */
@RequestMapping("/asset")
/**
 * コンストラクタを使う場合は「@RequiredArgsConstructor」をクラスに付けて挿入してもらいたいフィールドをfinalで宣言する
 */
@RequiredArgsConstructor
public class AssetController {
    /**
     * UseCase層のクラスをfinalで宣言する
     */
    private final AssetService service;
    /**
     * @GetMappingや@PostMappingを書くことでルーティングができる
     */
    @GetMapping("/cio/unprocessedOut")
    public List<UserCashOut> findUnprocessedCashOut() {
        /**
         * stream()はコレクションの要素を1つずつ取り出して処理う宣言を行う
         */
        return service.findUnprocessedCashOut().stream()
                /**
                 * map()はインプット要素（List<CashInOut>）を1つずつ取り出してアウトプット（List<UserCashOut>）へ詰める処理を行う
                 */
                .map(UserCashOut::of)
                /**
                 * stream型になっているため、List型に戻す
                 */
                .toList();
    }

    @PostMapping("/cio/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(
            @RequestBody @Valid UserRegCashOut p) {
        var accountId = ActorSession.actor().id();
        return ResponseEntity.ok(Map.of("id", service.withdraw(p.toParam(accountId))));
    }

    public static record UserRegCashOut(
            @Currency String currency,
            @AbsAmount BigDecimal absAmount) implements Dto {
        public RegCashOut toParam(String accountId) {
            return RegCashOut.builder()
                    .accountId(accountId)
                    .currency(this.currency)
                    .absAmount(this.absAmount)
                    .build();
        }
    }

    @Builder
    public static record UserCashOut(
            String id,
            String currency,
            BigDecimal absAmount,
            LocalDate requestDay,
            LocalDateTime requestDate,
            LocalDate eventDay,
            LocalDate valueDay,
            ActionStatusType statusType,
            LocalDateTime updateDate,
            Long cashflowId) implements Dto {
        public static UserCashOut of(final CashInOut cio) {
            return UserCashOut.builder()
                    .id(cio.getId())
                    .currency(cio.getCurrency())
                    .absAmount(cio.getAbsAmount())
                    .requestDay(cio.getRequestDay())
                    .requestDate(cio.getRequestDate())
                    .eventDay(cio.getEventDay())
                    .valueDay(cio.getValueDay())
                    .statusType(cio.getStatusType())
                    .updateDate(cio.getUpdateDate())
                    .cashflowId(cio.getCashflowId())
                    .build();
        }
    }

}
