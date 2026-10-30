<template>
  <div :class="{'hidden': hidden, 'with-extra': hasExtra}" class="pagination-container">
    <span v-if="hasExtra" class="pagination-summary">共 {{ total }} 条</span>
    <span v-if="hasExtra" class="pagination-extra">
      <slot name="extra" />
    </span>
    <el-pagination
      class="pagination-body"
      :background="background"
      :current-page.sync="currentPage"
      :page-size.sync="pageSize"
      :layout="realLayout"
      :page-sizes="pageSizes"
      :pager-count="pagerCount"
      :total="total"
      v-bind="$attrs"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script>
import { scrollTo } from '@/utils/scroll-to'

export default {
  name: 'Pagination',
  props: {
    total: {
      required: true,
      type: Number
    },
    page: {
      type: Number,
      default: 1
    },
    limit: {
      type: Number,
      default: 20
    },
    pageSizes: {
      type: Array,
      default() {
        return [10, 20, 30, 50]
      }
    },
    // 移动端页码按钮的数量端默认值5
    pagerCount: {
      type: Number,
      default: document.body.clientWidth < 992 ? 5 : 7
    },
    layout: {
      type: String,
      default: 'total, sizes, prev, pager, next, jumper'
    },
    background: {
      type: Boolean,
      default: true
    },
    autoScroll: {
      type: Boolean,
      default: true
    },
    hidden: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
    }
  },
  computed: {
    // 是否使用了 extra 插槽（例如导出按钮），有插槽时总条数由组件自身渲染
    hasExtra() {
      return !!(this.$slots && this.$slots.extra)
    },
    // 有 extra 插槽时去掉 layout 里的 total，避免条数重复
    realLayout() {
      if (!this.hasExtra) {
        return this.layout
      }
      return this.layout
        .split(',')
        .map(item => item.trim())
        .filter(item => item && item !== 'total')
        .join(', ')
    },
    currentPage: {
      get() {
        return this.page
      },
      set(val) {
        this.$emit('update:page', val)
      }
    },
    pageSize: {
      get() {
        return this.limit
      },
      set(val) {
        this.$emit('update:limit', val)
      }
    }
  },
  methods: {
    handleSizeChange(val) {
      if (this.currentPage * val > this.total) {
        this.currentPage = 1
      }
      this.$emit('pagination', { page: this.currentPage, limit: val })
      if (this.autoScroll) {
        scrollTo(0, 800)
      }
    },
    handleCurrentChange(val) {
      this.$emit('pagination', { page: val, limit: this.pageSize })
      if (this.autoScroll) {
        scrollTo(0, 800)
      }
    }
  }
}
</script>

<style scoped>
.pagination-container {
  background: #fff;
  padding: 32px 16px;
}
.pagination-container.hidden {
  display: none;
}
/* 带 extra 插槽时：共 N 条 + 自定义操作 + 分页条 整体居中一行。
   ruoyi.scss 里 `.pagination-container .el-pagination` 是 absolute + right:0，
   这里必须改回 static，否则分页条会脱离文档流并与左侧内容重叠。 */
.pagination-container.with-extra {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: center;
  height: auto;
  min-height: 32px;
  text-align: center;
}
.pagination-container.with-extra .pagination-summary {
  margin-right: 12px;
  color: #606266;
  font-size: 13px;
  font-weight: 400;
  white-space: nowrap;
}
.pagination-container.with-extra .pagination-extra {
  display: inline-flex;
  align-items: center;
  margin-right: 12px;
}
.pagination-container.with-extra .pagination-body {
  position: static;
  right: auto;
  flex: 0 0 auto;
}
</style>
