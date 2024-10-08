package com.insa.TeamOpsSystem.sixmonthchekelist;

import com.insa.TeamOpsSystem.jwt.PaginatedResultsRetrievedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/sixmclist")
@RequiredArgsConstructor
public class SixMCListController {
    private final SixMCListService sixmclistService;
    private final ApplicationEventPublisher eventPublisher;
    private final SixMCListMapper sixmclistMapper;

    @PostMapping
    public SixMCList createTraffics(@RequestBody SixMCList sixmclist, UsernamePasswordAuthenticationToken token) throws IllegalAccessException {
        return sixmclistService.createTraffics(sixmclist, token);
    }

    @GetMapping("/{id}")
    public SixMCList getTrafficById(@PathVariable("id") long id) {
        return sixmclistService.getTrafficById(id);
    }


    @GetMapping
    public Page<SixMCList> getAllTraffics(Pageable pageable, UsernamePasswordAuthenticationToken token) {
        return sixmclistService.getAllTraffics(pageable, token);
    }

    @PutMapping("/{id}")
    public SixMCList updateTrafficById(@PathVariable("id") long id, @RequestBody SixMCList sixmclist, UsernamePasswordAuthenticationToken token) throws IllegalAccessException {

        return sixmclistService.updateTrafficById(id, sixmclist, token);
    }

    @DeleteMapping("/{id}")
    public void deleteTrafficById(@PathVariable("id") long id, UsernamePasswordAuthenticationToken token) {
        sixmclistService.deleteTrafficById(id, token);
    }
    @GetMapping("/{from}/{to}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PagedModel<SixMCListDtos>> findAllByCreatedAtBetween(
       @PathVariable("from")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from
            , @PathVariable("to")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
            ,UsernamePasswordAuthenticationToken token
            , Pageable pageable,
            PagedResourcesAssembler assembler,
            UriComponentsBuilder uriBuilder,
            final HttpServletResponse response) {
        eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<>(

                SixMCListDtos.class, uriBuilder, response, pageable.getPageNumber(),
                sixmclistService.findAllByCreatedAtBetween(from,to,token, pageable).getTotalPages(), pageable.getPageSize()));
        return new ResponseEntity<PagedModel<SixMCListDtos>>(assembler.toModel(
                sixmclistService.findAllByCreatedAtBetween(from,to,token, pageable).map(
                        sixmclistMapper::toSixMCListDtos)), HttpStatus.OK);
    }
}


