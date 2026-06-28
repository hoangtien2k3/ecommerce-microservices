"use client";

import { cn } from "@ecommerce/lib/utils";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "../Button";
import { SearchBar } from "./SearchBar";

interface Column<T> {
  key: string;
  header: string;
  render: (item: T) => React.ReactNode;
  className?: string;
  headerClassName?: string;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  isLoading?: boolean;
  search?: string;
  onSearchChange?: (value: string) => void;
  searchPlaceholder?: string;
  emptyMessage?: string;
  page?: number;
  totalPages?: number;
  onPageChange?: (page: number) => void;
  rowKey: (item: T) => string | number;
  className?: string;
}

export function DataTable<T>({
  columns,
  data,
  isLoading,
  search,
  onSearchChange,
  searchPlaceholder,
  emptyMessage,
  page,
  totalPages,
  onPageChange,
  rowKey,
  className,
}: DataTableProps<T>) {
  return (
    <div className={cn("space-y-5", className)}>
      {onSearchChange && (
        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <SearchBar
            value={search ?? ""}
            onChange={onSearchChange}
            placeholder={searchPlaceholder}
            className="max-w-sm"
          />
        </div>
      )}

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                {columns.map((col) => (
                  <th
                    key={col.key}
                    className={cn(
                      "text-left px-4 py-3 font-semibold text-gray-700",
                      col.headerClassName
                    )}
                  >
                    {col.header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {isLoading
                ? Array.from({ length: 5 }).map((_, i) => (
                    <tr key={i} className="border-b border-gray-100">
                      {columns.map((col) => (
                        <td key={col.key} className="px-4 py-3">
                          <div className="h-4 bg-gray-200 rounded animate-pulse" />
                        </td>
                      ))}
                    </tr>
                  ))
                : data.length === 0
                ? (
                    <tr>
                      <td colSpan={columns.length} className="text-center py-12 text-gray-500">
                        {emptyMessage ?? "No data available"}
                      </td>
                    </tr>
                  )
                : data.map((item) => (
                    <tr
                      key={rowKey(item)}
                      className="border-b border-gray-100 hover:bg-gray-50 transition-colors"
                    >
                      {columns.map((col) => (
                        <td key={col.key} className={cn("px-4 py-3", col.className)}>
                          {col.render(item)}
                        </td>
                      ))}
                    </tr>
                  ))}
            </tbody>
          </table>
        </div>

        {page !== undefined && totalPages !== undefined && totalPages > 1 && onPageChange && (
          <div className="flex items-center justify-center gap-2 p-4 border-t border-gray-200">
            <Button
              variant="outline"
              size="sm"
              disabled={page === 0}
              onClick={() => onPageChange(page - 1)}
            >
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <span className="px-3 py-1.5 text-sm text-gray-600">
              Page {page + 1} of {totalPages}
            </span>
            <Button
              variant="outline"
              size="sm"
              disabled={page === totalPages - 1}
              onClick={() => onPageChange(page + 1)}
            >
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}
