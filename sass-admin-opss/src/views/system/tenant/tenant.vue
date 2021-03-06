<template>
  <div>
    <Card>
      <Form :model="form" inline label-colon>
        <FormItem>
          <Input v-model="form.tenantId" placeholder="租户编码"></Input>
        </FormItem>
        <FormItem>
          <Input v-model="form.tenantName" placeholder="租户名称"></Input>
        </FormItem>
        <FormItem>
          <Button type="primary" @click="hdlQuery">查询</Button>
          <Button style="margin-left: 8px" @click="reset">重置</Button>
        </FormItem>
      </Form>
      <Row type="flex" :gutter="20" class="row-operation">
        <Col
          ><Button icon="md-add" type="primary" @click="hdlSingleCreate"
            >新增</Button
          >
        </Col>
        <Col
          ><Button
            icon="md-trash"
            type="error"
            @click="hdlDelete(deleteOrgCodes)"
            >删除</Button
          ></Col
        >
      </Row>
      <Table
        stripe
        border
        ref="tables"
        :columns="columns"
        :data="tableData"
        :loading="isLoading"
        @on-selection-change="hdlSelectionChange"
      >
        <template slot-scope="{ row }" slot="state">
          <span>{{ $mapd("organizationState", row.state) }}</span>
        </template>
        <template slot-scope="{ row }" slot="create_time">
          <span>{{ row.gmtCreated | timeFilter }}</span>
        </template>
        <template slot-scope="{ row }" slot="action">
          <Button
            type="primary"
            size="small"
            style="margin-right: 5px"
            @click="hdlSingleModified(row)"
          >
            修改
          </Button>
          <Button type="error" size="small" @click="hdlDelete([row.tenantId])">
            删除
          </Button>
        </template>
      </Table>
      <div style="margin: auto; text-align: right;padding-top:10px">
        <Page
          :total="total"
          :current="page"
          :page-size-opts="[10, 20, 50, 100]"
          @on-change="changePage"
          @on-page-size-change="changePageSize"
          show-sizer
          show-elevator
          show-total
          transfer
        ></Page>
      </div>

      <tenant-modify
        :type="modifyType"
        ref="modifyModal"
        v-model="showModifyModal"
        @on-cancel="showModifyModal = false"
        @tenant-modified="hdlModified"
      ></tenant-modify>
    </Card>
  </div>
</template>

<script>
import { getTenantList, delTenant, queryTenant } from "@/api";
import { tenantModify } from "./components";
export default {
  name: "sys-tenant",
  components: {
    tenantModify
  },
  computed: {
    deleteOrgCodes() {
      return this.tbSelection.map(item => item.tenantId);
    }
  },
  data() {
    return {
      modifyType: "create",
      showModifyModal: false,
      isLoading: false,
      pageSize: 10,
      total: 0,
      page: 1,
      form: {
        tenantId: "",
        tenantName: "",
        state: ""
      },
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        { title: "租户编码", key: "tenantId", align: "center" },
        { title: "租户名称", key: "tenantName", align: "center" },
        { title: "备注", key: "remark", align: "center" },
        { title: "状态", slot: "state", align: "center" },
        {
          title: "创建时间",
          key: "gmtCreated",
          align: "center",
          slot: "create_time"
        },
        { title: "操作", slot: "action", align: "center", width: 150 }
      ],
      tableData: [],
      tbSelection: []
    };
  },
  methods: {
    hdlQuery() {
      this.changePage(1);
    },
    changePage(pageNum) {
      this.isLoading = true;
      let pageSize = this.pageSize;
      let pageIndex = pageNum;
      let data = {
        code: this.form.tenantId,
        name: this.form.tenantName,
        state: this.form.state
      };
      getTenantList({ pageIndex, pageSize, ...data })
        .then(res => {
          let { data } = res;
          this.page = pageIndex;
          this.tableData = data.items;
          this.total = Number(data.total);
        })
        .finally(() => (this.isLoading = false));
    },
    changePageSize(pageSize) {
      this.pageSize = pageSize;
      this.changePage(1);
    },
    reset() {
      this.form.tenantName = "";
      this.form.tenantId = "";
      this.form.state = "";
      this.hdlQuery();
    },
    hdlDelete(codes) {
      let self = this;
      if (codes.length > 0) {
        this.$Modal.confirm({
          title: "确认删除？",
          content: `确定删除选中记录?`,
          onOk() {
            delTenant({ codes }).then(() => {
              this.$Message.success("删除成功！");
              self.hdlQuery();
            });
          }
        });
      } else {
        this.$Message.warning("请选择一条记录!");
      }
    },
    hdlSingleCreate() {
      this.modifyType = "create";
      this.$refs.modifyModal.setData({
        obj: {},
        remark: "",
        state: ""
      });
      this.showModifyModal = true;
    },
    hdlSingleModified(data) {
      queryTenant({ code: data.tenantId }).then(res => {
        this.modifyType = "update";
        this.$refs.modifyModal.setData({
          obj: res.data,
          remark: data.remark,
          state: data.state
        });
        this.showModifyModal = true;
      });
    },
    hdlSelectionChange(selection) {
      this.tbSelection = selection;
    },
    hdlModified(type) {
      this.$Message.success(`${type == "create" ? "新增" : "修改"}成功！`);
      this.hdlQuery();
    }
  },
  mounted() {
    this.changePage(1);
  }
};
</script>

<style lang="less" scoped>
.row-operation {
  padding: 10px 0;
}
</style>
