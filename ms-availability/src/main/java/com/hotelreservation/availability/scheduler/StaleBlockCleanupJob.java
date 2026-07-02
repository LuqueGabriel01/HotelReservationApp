package com.hotelreservation.availability.scheduler;

import com.hotelreservation.availability.models.entities.RoomBlock;
import com.hotelreservation.availability.repositories.AvailabilityRepository;
import com.hotelreservation.availability.repositories.BlockRepository;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Scheduled background job responsible for cleaning up expired room blocks. */
@Component
@RequiredArgsConstructor
public class StaleBlockCleanupJob {

  private static final Logger LOG = LoggerFactory.getLogger(StaleBlockCleanupJob.class);
  private final BlockRepository blockRepository;
  private final AvailabilityRepository availabilityRepository;

  /**
   * Periodically removes expired room blocks and releases their associated availability dates back
   * to the inventory.
   *
   * <p>Runs automatically at a fixed rate of every 60 seconds.
   */
  @Scheduled(fixedRate = 60000)
  @Transactional
  public void cleanupExpiredBlock() {
    List<RoomBlock> expiredBlocks =
        blockRepository.findByExpiresAtBefore(Timestamp.from(Instant.now()));

    if (expiredBlocks.isEmpty()) {
      return;
    }

    for (RoomBlock block : expiredBlocks) {
      List<LocalDate> dates = block.getCheckIn().datesUntil(block.getCheckOut()).toList();
      availabilityRepository.deleteByRoomIdAndDateIn(block.getRoomId(), dates);
    }

    blockRepository.deleteAll(expiredBlocks);
    LOG.info("Cleaned up {} expired room blocks", expiredBlocks.size());
  }
}
