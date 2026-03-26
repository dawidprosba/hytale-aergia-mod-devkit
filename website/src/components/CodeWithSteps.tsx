import React, { useCallback, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { Highlight, type Language } from 'prism-react-renderer';
import { usePrismTheme } from '@docusaurus/theme-common';
import clsx from 'clsx';
import styles from './CodeWithSteps.module.css';

// Matches // (N) inside a token
const STEP_RE = /^([\s\S]*?)\/\/ \((\d+)\)([\s\S]*)$/;

// Magic comment directives
const ADD_NEXT_RE    = /^\s*\/\/ add-next-line\s*$/;
const ADD_START_RE   = /^\s*\/\/ add-start\s*$/;
const ADD_END_RE     = /^\s*\/\/ add-end\s*$/;
const REMOVE_NEXT_RE = /^\s*\/\/ remove-next-line\s*$/;
const REMOVE_START_RE = /^\s*\/\/ remove-start\s*$/;
const REMOVE_END_RE   = /^\s*\/\/ remove-end\s*$/;

function processCode(raw: string): {
  code: string;
  addLines: Set<number>;
  removeLines: Set<number>;
} {
  const addLines = new Set<number>();
  const removeLines = new Set<number>();
  let addNext = false;
  let removeNext = false;
  let inAddBlock = false;
  let inRemoveBlock = false;
  const out: string[] = [];

  for (const line of raw.split('\n')) {
    if (ADD_START_RE.test(line))    { inAddBlock = true; continue; }
    if (ADD_END_RE.test(line))      { inAddBlock = false; continue; }
    if (REMOVE_START_RE.test(line)) { inRemoveBlock = true; continue; }
    if (REMOVE_END_RE.test(line))   { inRemoveBlock = false; continue; }
    if (ADD_NEXT_RE.test(line))     { addNext = true; continue; }
    if (REMOVE_NEXT_RE.test(line))  { removeNext = true; continue; }

    const i = out.length;
    if (inAddBlock || addNext)       { addLines.add(i);    addNext = false; }
    if (inRemoveBlock || removeNext) { removeLines.add(i); removeNext = false; }
    out.push(line);
  }

  return { code: out.join('\n'), addLines, removeLines };
}

interface TooltipPos { x: number; y: number }

function Pill({ number, tooltip }: { number: string; tooltip: string }) {
  const ref = useRef<HTMLSpanElement>(null);
  const [pos, setPos] = useState<TooltipPos | null>(null);

  return (
    <>
      <span
        ref={ref}
        className={styles.pill}
        onMouseEnter={() => {
          const rect = ref.current?.getBoundingClientRect();
          if (rect) setPos({ x: rect.left + rect.width / 2, y: rect.top });
        }}
        onMouseLeave={() => setPos(null)}
      >
        {number}
      </span>
      {pos && createPortal(
        <div
          className={styles.tooltip}
          style={{ left: pos.x, top: pos.y }}
        >
          {tooltip}
        </div>,
        document.body,
      )}
    </>
  );
}

interface Props {
  language?: Language;
  steps: string[];
  children: string;
}

export default function CodeWithSteps({ language = 'kotlin', steps, children }: Props) {
  const theme = usePrismTheme();
  const [copied, setCopied] = useState(false);

  const { code, addLines, removeLines } = processCode(children.trimEnd());

  const handleCopy = useCallback(() => {
    const clean = code.replace(/\/\/ \(\d+\)/g, '').replace(/ +$/gm, '');
    navigator.clipboard.writeText(clean).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 1000);
    });
  }, [code]);

  return (
    <div className={styles.wrapper}>
      <div className={styles.codeBlock}>
        <button
          className={clsx(styles.copyButton, copied && styles.copyButtonCopied)}
          onClick={handleCopy}
          aria-label={copied ? 'Copied' : 'Copy code to clipboard'}
          title="Copy"
        >
          <svg className={styles.copyIcon} viewBox="0 0 24 24"><path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/></svg>
          <svg className={styles.successIcon} viewBox="0 0 24 24"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>
        </button>

        <Highlight theme={theme} code={code} language={language}>
          {({ className, style, tokens, getLineProps, getTokenProps }) => (
            <pre className={clsx(styles.pre, className)} style={style}>
              <code>
                {tokens.map((line, lineIdx) => (
                  <span
                    key={lineIdx}
                    {...getLineProps({ line })}
                    className={clsx(
                      getLineProps({ line }).className,
                      styles.line,
                      addLines.has(lineIdx) && 'code-block-add-line',
                      removeLines.has(lineIdx) && 'code-block-remove-line',
                    )}
                  >
                    {line.map((token, tokenIdx) => {
                      const match = STEP_RE.exec(token.content);
                      if (match) {
                        const props = getTokenProps({ token });
                        return (
                          <React.Fragment key={tokenIdx}>
                            {match[1] && <span {...props}>{match[1]}</span>}
                            <Pill number={match[2]} tooltip={steps[Number(match[2]) - 1] ?? ''} />
                            {match[3] && <span {...props}>{match[3]}</span>}
                          </React.Fragment>
                        );
                      }
                      return <span key={tokenIdx} {...getTokenProps({ token })} />;
                    })}
                  </span>
                ))}
              </code>
            </pre>
          )}
        </Highlight>
      </div>

      <ol className={styles.steps}>
        {steps.map((step, i) => (
          <li key={i}>{step}</li>
        ))}
      </ol>
    </div>
  );
}
