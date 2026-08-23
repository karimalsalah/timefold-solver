# ADOPTION — timefold-solver (STEADYWRK estate)

Fork of [`TimefoldAI/timefold-solver`](https://github.com/TimefoldAI/timefold-solver). Adopted 2026-08-23 by the `moat` lane.
Estate record: `docs/strategy/OSS-ADOPTION-2026-08-23.md` in `karimalsalah/steadywrk` (PR #1465).

## What it is (verified live 2026-08-23)

Apache-2.0 · 1,760★ · last push 2026-08-21 · not archived · not a fork.
**Java / Maven** (`pom.xml`), multi-module. The largest fork in the set. A mature **constraint-satisfaction and optimization solver** — vehicle routing, employee rostering, job scheduling.

## Why we took it — the strategic core of the set

Live competitive research on 2026-08-23 produced the single sharpest finding about our own category:

> **"Agentic at the phone, algorithmic at the board."** Across every incumbent verified — ServiceTitan, Jobber, Housecall Pro, Workiz, Zuper — the LLM agent lives at **intake** (answering calls, texts, web chat), while the actual **job-to-technician assignment decision is still classic optimization.**

Nobody has made the assignment decision agentic. That is the unoccupied position, and it is unoccupied for a good reason: **neither half works alone.**

- A pure LLM assigning jobs is unaccountable and will violate hard constraints (licensing, travel time, shift legality).
- A pure solver cannot weigh the soft, contextual facts that decide real dispatch quality — this customer is angry, this technician handled them before, this building needs someone who speaks the tenant's language.

**The pairing is the position:** the solver owns hard constraints and produces a feasible set; the LLM layer chooses within that set on soft context and explains itself. Feasibility is guaranteed by construction, and judgement is auditable.

**What we take:** the solver core and its constraint-modelling API. Not the enterprise tooling, not the benchmarking UI.

## Sequencing

**Fifth of five, last deliberately.** Largest surface, highest payoff, and the only one that needs a **real design pass before any code** — the solver/LLM boundary is an architecture decision, not an integration chore. Do not start this one by writing code.

## Isolation

| Control | This fork |
|---|---|
| **Sandbox root** | `D:\forks\timefold-solver`. Never `C:`, never `~/.claude`, never `~/.cursor`. |
| **JVM/Maven footprint** | Maven writes to `~/.m2` **by default and by design**. Point it at a sandbox-local repository (`-Dmaven.repo.local=D:\forks\.m2`) so the adoption does not silently populate the home directory. |
| **Kill-switch** | Delete `D:\forks\timefold-solver` and the sandbox-local `.m2`. |
| **Credentials** | **None.** A solver has no legitimate use for a credential. |
| **Egress** | Expected **none at solve time** — it is an offline optimizer. Maven reaches Central during build; the solver itself should not open a socket. **Verify, do not assume.** |
| **Data** | Dry runs use **synthetic or anonymised** dispatch data. Never real customer names, addresses, or phone numbers in a sandbox experiment. |

## Required before any integration — the dry-run gate

1. **Filesystem diff.** Before/after on `~/.claude`, `~/.cursor/mcp.json` (hash), home root, **and `~/.m2`** — the Maven cache is this fork's most likely home-directory footprint.
2. **Egress check.** Confirm no network activity during a solve (build excluded).
3. **Capability proof.** Model a *real* steadywrk dispatch constraint set — skills, travel, time windows, licensing — and confirm the solver produces feasible assignments. **README claims are NOT verified** — only license, liveness, and non-archived status are.
4. **Design pass, before code.** Write down the solver/LLM boundary: what is a **hard** constraint (solver, non-negotiable) versus a **soft** preference (LLM, contextual). Getting this backwards produces a system that is unaccountable *and* infeasible. This gate is a document, not a build.
5. **Explainability check.** An assignment we cannot explain to a technician who was passed over is not shippable. Dispatch decisions are made about people.

## Relationship to M2 (the outcome ledger)

This fork is only as good as the data behind it. As measured on 2026-08-23, the dispatch ledger is **complete on operator paths, blind on the ingest path, coarse on outcome types** — `dispatch_status` cannot express `failed`, `disputed`, `expired`, or `no_show`, so all of them flatten into `cancelled`.

**A solver tuned against outcome data that cannot distinguish a no-show from a dispute will optimise for the wrong thing.** Fix the ledger first. This is a sequencing dependency, not a footnote.

## Never

- Feed it real customer PII in a sandbox experiment.
- Let Maven populate the user-global `~/.m2` during adoption work.
- Point it at the live `C:\Users\youso\dev\steadywrk` checkout.
- Ship an assignment the system cannot explain.
- Merge upstream blindly — re-run the gate after every version bump.

## Upstream

`upstream` = `TimefoldAI/timefold-solver`. We track it; we do not push to it. Contributions go upstream as normal PRs under Apache-2.0.
