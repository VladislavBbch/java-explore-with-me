package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.model.StatisticInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Hit, Long> {
    @Query(value = "select min(application) as app, uri, count(*) as hits " +
            "from hits " +
            "where timestamp > ?1 and timestamp < ?2 " +
            "group by uri " +
            "order by hits desc", nativeQuery = true)
    List<StatisticInfo> statisticByAllUri(LocalDateTime start, LocalDateTime end);

    @Query(value = "select min(uniqueIpToUri.app) as app, uniqueIpToUri.uri, count(*) as hits " +
            "from (select min(h.application) as app, h.uri, h.ip " +
            "from hits as h " +
            "where h.timestamp > ?1 and h.timestamp < ?2 " +
            "group by h.uri, h.ip) as uniqueIpToUri " +
            "group by uniqueIpToUri.uri " +
            "order by hits desc", nativeQuery = true)
    List<StatisticInfo> statisticByAllUriAndUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "select min(application) as app, uri, count(*) as hits " +
            "from hits " +
            "where timestamp > ?1 and timestamp < ?2 " +
            "and uri in (?3) " +
            "group by uri " +
            "order by hits desc", nativeQuery = true)
    List<StatisticInfo> statisticByUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select min(uniqueIpToUri.app) as app, uniqueIpToUri.uri, count(*) as hits " +
            "from (select min(h.application) as app, h.uri, h.ip " +
            "from hits as h " +
            "where h.timestamp > ?1 and h.timestamp < ?2 " +
            "and h.uri in (?3) " +
            "group by h.uri, h.ip) as uniqueIpToUri " +
            "group by uniqueIpToUri.uri " +
            "order by hits desc", nativeQuery = true)
    List<StatisticInfo> statisticByUriInAndUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
