#!/bin/bash
set -e

RELEASE_BRANCH="test/rele"
MAX_COMMITS=10
SKIP_MARKER="(nr)"

git fetch --all

# Check if release branch exists
if ! git ls-remote --heads origin "$RELEASE_BRANCH" | grep -q "refs/heads/$RELEASE_BRANCH"; then
  echo "Release branch does not exist"
  echo "RELEASE_BRANCH_MISSING=true" >> "$GITHUB_ENV"
  exit 0
fi

# Get the 10 most recent commits in master not in release
COMMITS=$(git rev-list origin/master ^origin/"$RELEASE_BRANCH" --reverse | tail -n "$MAX_COMMITS")

if [ -z "$COMMITS" ]; then
  echo "No commits to cherry-pick"
  exit 0
fi

git checkout "$RELEASE_BRANCH"

FAILED=false

for COMMIT in $COMMITS; do
  MSG=$(git log -1 --pretty=%s "$COMMIT")

  if [[ "$MSG" == *"$SKIP_MARKER"* ]]; then
    echo "Skipping $COMMIT due to '$SKIP_MARKER' marker"
    continue
  fi

  echo "Cherry-picking $COMMIT: $MSG"

  if git cherry-pick "$COMMIT"; then
    continue
  else
    echo "Cherry-pick failed for $COMMIT"
    FAILED_COMMIT="$COMMIT"
    FAILED=true
    git cherry-pick --abort
    break
  fi
done

if [ "$FAILED" = false ]; then
  git push origin "$RELEASE_BRANCH"
  exit 0
fi

# Create temp branch from master
SHORT_HASH=$(echo "$FAILED_COMMIT" | cut -c1-5)
TEMP_BRANCH="release-tmp/for_${SHORT_HASH}"

git checkout -b "$TEMP_BRANCH" origin/master
git push origin "$TEMP_BRANCH"

# Export values for workflow
echo "MERGE_FAILED=true" >> "$GITHUB_ENV"
echo "TEMP_BRANCH=$TEMP_BRANCH" >> "$GITHUB_ENV"
echo "OFFENDING_COMMIT=$FAILED_COMMIT" >> "$GITHUB_ENV"
